package com.github.rag.tutorials.chatbot.service;

import com.github.rag.tutorials.chatbot.model.PreBooking;
import com.github.rag.tutorials.chatbot.model.PreBookingStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class PreBookingService {

    private final ConcurrentMap<String, PreBooking> preBookings = new ConcurrentHashMap<>();

    public PreBooking createPreBooking(String firstName, String lastName, String phoneNumber, String email,
                                       LocalDateTime checkIn, LocalDateTime checkOut, Integer totalPriceEur) {
        PreBooking preBooking = new PreBooking();
        preBooking.setPreBookingId(generatePreBookingId());
        preBooking.setFirstName(firstName);
        preBooking.setLastName(lastName);
        preBooking.setPhoneNumber(phoneNumber);
        preBooking.setEmail(email);
        preBooking.setCheckIn(checkIn);
        preBooking.setCheckOut(checkOut);
        preBooking.setBookingDate(checkIn);
        preBooking.setTotalPriceEur(totalPriceEur);
        preBooking.setCreatedAt(LocalDateTime.now());
        preBooking.setStatus(PreBookingStatus.PENDING);
        preBookings.put(preBooking.getPreBookingId(), preBooking);
        return preBooking;
    }

    public PreBooking getById(String preBookingId) {
        PreBooking preBooking = preBookings.get(preBookingId);
        if (preBooking == null) {
            throw new NoSuchElementException("Pre-booking not found: " + preBookingId);
        }
        return preBooking;
    }

    public PreBooking markAsConfirmed(String preBookingId, String bookingNumber) {
        PreBooking preBooking = getById(preBookingId);
        if (preBooking.isConfirmed()) {
            return preBooking;
        }
        preBooking.setBookingNumber(bookingNumber);
        preBooking.setConfirmedAt(LocalDateTime.now());
        preBooking.setStatus(PreBookingStatus.CONFIRMED);
        return preBooking;
    }

    private String generatePreBookingId() {
        return "PB-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
