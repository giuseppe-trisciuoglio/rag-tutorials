package com.github.rag.tutorials.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckoutPreBookingResponse {
    private String preBookingId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime bookingDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Integer totalPriceEur;
    private String status;
    private String bookingNumber;
}
