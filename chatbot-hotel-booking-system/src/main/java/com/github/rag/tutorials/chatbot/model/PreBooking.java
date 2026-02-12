package com.github.rag.tutorials.chatbot.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PreBooking {
    private String preBookingId;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer totalPriceEur;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private String bookingNumber;
    private PreBookingStatus status;

    public boolean isConfirmed() {
        return PreBookingStatus.CONFIRMED.equals(status);
    }
}
