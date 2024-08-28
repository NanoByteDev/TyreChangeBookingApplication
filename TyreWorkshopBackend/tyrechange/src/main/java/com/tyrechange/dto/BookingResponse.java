package com.tyrechange.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingResponse {
    private String id;
    private String workshopName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;
    
    private String vehicleType;
    private String contactInformation;

    // Constructor
    public BookingResponse(String id, String workshopName, LocalDateTime bookingTime, String vehicleType, String contactInformation) {
        this.id = id;
        this.workshopName = workshopName;
        this.bookingTime = bookingTime;
        this.vehicleType = vehicleType;
        this.contactInformation = contactInformation;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getFormattedBookingTime() {
        return bookingTime != null ? bookingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "N/A";
    }
}