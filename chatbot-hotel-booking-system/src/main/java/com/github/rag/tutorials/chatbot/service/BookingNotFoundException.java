package com.github.rag.tutorials.chatbot.service;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String bookingNumber) {
        super("Booking not found with booking number: " + bookingNumber);
    }
}
