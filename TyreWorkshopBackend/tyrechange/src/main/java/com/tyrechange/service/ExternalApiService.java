package com.tyrechange.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tyrechange.dto.AvailableTime;
import com.tyrechange.dto.BookingRequest;
import com.tyrechange.dto.BookingResponse;
import com.tyrechange.model.TyreWorkshop;

@Service
public class ExternalApiService {
    private static final Logger logger = LoggerFactory.getLogger(ExternalApiService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final WorkshopService workshopService;
    private final ObjectMapper objectMapper;


    @Autowired
    public ExternalApiService(RestTemplate restTemplate, WorkshopService workshopService, ObjectMapper objectMapper, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.workshopService = workshopService;
        this.objectMapper = objectMapper;
        this.xmlMapper = xmlMapper;
    }

    public List<AvailableTime> getAvailableTimes(String workshopName, LocalDate from, LocalDate until) {
        logger.info("Fetching available times for workshop: {}, from: {}, until: {}", workshopName, from, until);
        
        TyreWorkshop workshop = workshopService.getWorkshopByName(workshopName);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.Collections.singletonList(MediaType.ALL));

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(workshop.getApiUrl())
            .path("/api/v" + workshop.getApiVersion() + "/tire-change-times");

        if ("1".equals(workshop.getApiVersion())) {
            builder.path("/available")
                   .queryParam("from", from)
                   .queryParam("until", until);
        } else if ("2".equals(workshop.getApiVersion())) {
            builder.queryParam("from", from)
                   .queryParam("amount", 100)  // Adjust as needed
                   .queryParam("page", 1);     // Start with page 1
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);
        
        try {
            String response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                String.class
            ).getBody();

            logger.info("Received response from external API: {}", response);
            return parseAvailableTimes(response);
        } catch (RestClientException e) {
            logger.error("Error calling external API for workshop: {}", workshopName, e);
            throw new RuntimeException("Error fetching available times from external API", e);
        }
    }

    private List<AvailableTime> parseAvailableTimes(String response) throws RestClientException {
        List<AvailableTime> availableTimes = new ArrayList<>();
        try {
            if (response.trim().startsWith("<")) {
                // XML response
                
                JsonNode root = xmlMapper.readTree(response);
                JsonNode times = root.get("availableTime");
                if (times.isArray()) {
                    for (JsonNode time : times) {
                        String uuid = time.get("uuid").asText();
                        LocalDateTime dateTime = LocalDateTime.parse(time.get("time").asText(), formatter);
                        availableTimes.add(new AvailableTime(uuid, dateTime));
                    }
                }
            } else if (response.trim().startsWith("[")) {
                // JSON response
                JsonNode root = objectMapper.readTree(response);
                if (root.isArray()) {
                    for (JsonNode time : root) {
                        String id = time.get("id").asText();
                        LocalDateTime dateTime = LocalDateTime.parse(time.get("time").asText(), formatter);
                        boolean available = time.get("available").asBoolean();
                        if (available) {
                            availableTimes.add(new AvailableTime(id, dateTime));
                        }
                    }
                }
            } else {
                throw new RestClientException("Unexpected response format");
            }
        } catch (Exception e) {
            logger.error("Error parsing available times", e);
            throw new RestClientException("Error parsing available times", e);
        }
        return availableTimes;
    }

    public BookingResponse bookAppointment(BookingRequest bookingRequest) {
        logger.info("Booking appointment: {}", bookingRequest);
        TyreWorkshop workshop = workshopService.getWorkshopByName(bookingRequest.getWorkshopName());
        
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method;
        String url;
        String body;

        if ("1".equals(workshop.getApiVersion())) {
            // London server
            headers.setContentType(MediaType.TEXT_XML);
            headers.set("Accept", "text/xml");
            method = HttpMethod.PUT;
            url = UriComponentsBuilder.fromHttpUrl(workshop.getApiUrl())
                .path("/api/v1/tire-change-times/{timeId}/booking")
                .buildAndExpand(bookingRequest.getTimeId())
                .toUriString();
            body = String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<london.tireChangeBookingRequest>\n" +
                "\t<contactInformation>%s</contactInformation>\n" +
                "</london.tireChangeBookingRequest>",
                bookingRequest.getContactInformation()
            );
        } else {
            // Manchester server
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
            method = HttpMethod.POST;
            url = UriComponentsBuilder.fromHttpUrl(workshop.getApiUrl())
                .path("/api/v2/tire-change-times/{timeId}/booking")
                .buildAndExpand(bookingRequest.getTimeId())
                .toUriString();
            body = String.format("{\"contactInformation\": \"%s\"}", bookingRequest.getContactInformation());
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            logger.info("Sending booking request to URL: {}", url);
            logger.info("Request body: {}", body);
            logger.info("Request headers: {}", headers);
            
            String response = restTemplate.exchange(url, method, entity, String.class).getBody();
            logger.info("Booking response: {}", response);
            return parseBookingResponse(response, workshop.isXmlApi(), bookingRequest);
        } catch (RestClientException e) {
            logger.error("Error booking appointment for workshop: {}", bookingRequest.getWorkshopName(), e);
            throw new RuntimeException("Error booking appointment: " + e.getMessage(), e);
        }
    }

    private BookingResponse parseBookingResponse(String response, boolean isXmlApi, BookingRequest originalRequest) {
        try {
            String id;
            LocalDateTime time;
            if (isXmlApi) {
                // London server (XML)
                JsonNode root = xmlMapper.readTree(response);
                id = root.get("uuid").asText();
                time = parseDateTime(root.get("time").asText());
            } else {
                // Manchester server (JSON)
                JsonNode root = objectMapper.readTree(response);
                id = root.get("id").asText();
                time = parseDateTime(root.get("time").asText());
            }
            return new BookingResponse(
                id,
                originalRequest.getWorkshopName(),
                time,
                originalRequest.getVehicleType(),
                originalRequest.getContactInformation()
            );
        } catch (Exception e) {
            logger.error("Error parsing booking response: {}", response, e);
            throw new RestClientException("Error parsing booking response: " + e.getMessage(), e);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse date-time: {}. Attempting fallback parsing.", dateTimeStr);
            // Fallback: try parsing without the 'Z' if it's present
            if (dateTimeStr.endsWith("Z")) {
                dateTimeStr = dateTimeStr.substring(0, dateTimeStr.length() - 1);
            }
            return LocalDateTime.parse(dateTimeStr);
        }
    }
}