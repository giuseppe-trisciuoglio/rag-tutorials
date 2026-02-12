package com.github.rag.tutorials.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckoutConfirmationResponse {
    private String paymentStatus;
    private String preBookingId;
    private String bookingNumber;
    private Integer totalPriceEur;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private LocalDateTime confirmedAt;
    private String summary;
}
