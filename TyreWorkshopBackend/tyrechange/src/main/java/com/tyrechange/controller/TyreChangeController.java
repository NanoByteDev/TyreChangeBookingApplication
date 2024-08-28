package com.tyrechange.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tyrechange.dto.AvailableTime;
import com.tyrechange.dto.BookingRequest;
import com.tyrechange.dto.BookingResponse;
import com.tyrechange.model.TyreWorkshop;
import com.tyrechange.service.BookingService;
import com.tyrechange.service.ExternalApiService;
import com.tyrechange.service.WorkshopService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // Adjust if your Vue.js app is running on a different port
public class TyreChangeController {
    private static final Logger logger = LoggerFactory.getLogger(TyreChangeController.class);

    private final ExternalApiService externalApiService;
    private final BookingService bookingService;
    private final WorkshopService workshopService;

    @Autowired
    public TyreChangeController(BookingService bookingService, ExternalApiService externalApiService, WorkshopService workshopService) {
        this.bookingService = bookingService;
        this.externalApiService = externalApiService;
        this.workshopService = workshopService;
    }

    @GetMapping("/workshops")
    public ResponseEntity<List<TyreWorkshop>> getAllWorkshops() {
        logger.info("Fetching all workshops");
        List<TyreWorkshop> workshops = workshopService.getAllWorkshops();
        logger.info("Found {} workshops", workshops.size());
        return ResponseEntity.ok(workshops);
    }

    @GetMapping("/workshops/{name}/available-times")
    public ResponseEntity<List<AvailableTime>> getAvailableTimes(
            @PathVariable String name,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until,
            @RequestParam(required = false) String vehicleType) {
        logger.info("Fetching available times for workshop: {}, from: {}, until: {}, vehicleType: {}", name, from, until, vehicleType);
        try {
            List<AvailableTime> availableTimes = externalApiService.getAvailableTimes(name, from, until);
            return ResponseEntity.ok(availableTimes);
        } catch (Exception e) {
            logger.error("Error fetching available times", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        logger.info("Creating booking: {}", bookingRequest);
        try {
            BookingResponse bookingResponse = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(bookingResponse);
        } catch (Exception e) {
            logger.error("Error creating booking", e);
            return ResponseEntity.internalServerError().body("Error creating booking: " + e.getMessage());
        }
    }
}