package com.tyrechange.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.tyrechange.dto.BookingRequest;
import com.tyrechange.dto.BookingResponse;

class BookingServiceTest {

    @Mock
    private ExternalApiService externalApiService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBooking_SuccessfulBooking_ReturnsBookingResponse() {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setWorkshopName("Test Workshop");
        request.setTimeId("123");
        request.setVehicleType("Car");
        request.setContactInformation("John Doe, john@example.com");

        BookingResponse expectedResponse = new BookingResponse("456", "Test Workshop", 
            LocalDateTime.now(), "Car", "John Doe, john@example.com");

        when(externalApiService.bookAppointment(request)).thenReturn(expectedResponse);

        // Act
        BookingResponse actualResponse = bookingService.createBooking(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getWorkshopName(), actualResponse.getWorkshopName());
        assertEquals(expectedResponse.getVehicleType(), actualResponse.getVehicleType());
        assertEquals(expectedResponse.getContactInformation(), actualResponse.getContactInformation());

        verify(externalApiService, times(1)).bookAppointment(request);
    }

    @Test
    void createBooking_ExternalApiError_ThrowsRuntimeException() {
        // Arrange
        BookingRequest request = new BookingRequest();
        when(externalApiService.bookAppointment(request)).thenThrow(new RuntimeException("API Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(request));
        verify(externalApiService, times(1)).bookAppointment(request);
    }
}