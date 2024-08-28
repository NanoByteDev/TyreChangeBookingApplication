package com.tyrechange.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tyrechange.dto.BookingRequest;
import com.tyrechange.dto.BookingResponse;

@Service
public class BookingService {

    private final ExternalApiService externalApiService;

    @Autowired
    public BookingService(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    public BookingResponse createBooking(BookingRequest bookingRequest) {
        return externalApiService.bookAppointment(bookingRequest);
    }
}