package com.github.rag.tutorials.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreBookingCheckoutLink {
    private String preBookingId;
    private Integer totalPriceEur;
    private String checkoutUrl;
}
