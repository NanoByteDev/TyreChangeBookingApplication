package com.tyrechange.controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tyrechange.dto.AvailableTime;
import com.tyrechange.dto.BookingRequest;
import com.tyrechange.dto.BookingResponse;
import com.tyrechange.model.TyreWorkshop;
import com.tyrechange.service.BookingService;
import com.tyrechange.service.ExternalApiService;
import com.tyrechange.service.WorkshopService;

@WebMvcTest(TyreChangeController.class)
class TyreChangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkshopService workshopService;

    @MockBean
    private ExternalApiService externalApiService;

    @MockBean
    private BookingService bookingService;

    @Test
    void getAllWorkshops_ReturnsListOfWorkshops() throws Exception {
        // Arrange
        TyreWorkshop workshop = new TyreWorkshop();
        workshop.setName("Test Workshop");
        when(workshopService.getAllWorkshops()).thenReturn(Arrays.asList(workshop));

        // Act & Assert
        mockMvc.perform(get("/api/workshops"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("Test Workshop"));
    }

    @Test
    void getAvailableTimes_ReturnsListOfAvailableTimes() throws Exception {
        // Arrange
        Instant now = Instant.now();
        AvailableTime time = new AvailableTime("123", LocalDateTime.now());
        when(externalApiService.getAvailableTimes(anyString(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(time));

        // Act & Assert
        mockMvc.perform(get("/api/workshops/TestWorkshop/available-times")
               .param("from", "2023-05-01")
               .param("until", "2023-05-07"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value("123"))
               .andExpect(jsonPath("$[0].time").isNotEmpty());
    }

    @Test
    void createBooking_ReturnsBookingResponse() throws Exception {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setWorkshopName("Test Workshop");
        request.setTimeId("123");
        
        Instant now = Instant.now();
        BookingResponse response = new BookingResponse("456", "Test Workshop", LocalDateTime.now(), "Car", "John Doe");
        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("456"))
               .andExpect(jsonPath("$.bookingTime").isNotEmpty());
    }
}