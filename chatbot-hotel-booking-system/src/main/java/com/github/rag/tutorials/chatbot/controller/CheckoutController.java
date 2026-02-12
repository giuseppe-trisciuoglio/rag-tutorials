package com.github.rag.tutorials.chatbot.controller;

import com.github.rag.tutorials.chatbot.dto.CheckoutConfirmationResponse;
import com.github.rag.tutorials.chatbot.dto.CheckoutPreBookingResponse;
import com.github.rag.tutorials.chatbot.model.Booking;
import com.github.rag.tutorials.chatbot.model.PreBooking;
import com.github.rag.tutorials.chatbot.service.BookingService;
import com.github.rag.tutorials.chatbot.service.PreBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private static final DateTimeFormatter SUMMARY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PreBookingService preBookingService;
    private final BookingService bookingService;

    @GetMapping("/pre-bookings/{preBookingId}")
    public ResponseEntity<CheckoutPreBookingResponse> getPreBooking(@PathVariable String preBookingId) {
        try {
            PreBooking preBooking = preBookingService.getById(preBookingId);
            String status = preBooking.getStatus() != null ? preBooking.getStatus().name() : "PENDING";
            return ResponseEntity.ok(new CheckoutPreBookingResponse(
                    preBooking.getPreBookingId(),
                    preBooking.getFirstName(),
                    preBooking.getLastName(),
                    preBooking.getEmail(),
                    preBooking.getBookingDate(),
                    preBooking.getCheckIn(),
                    preBooking.getCheckOut(),
                    preBooking.getTotalPriceEur(),
                    status,
                    preBooking.getBookingNumber()
            ));
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    @PostMapping("/pre-bookings/{preBookingId}/confirm")
    public ResponseEntity<CheckoutConfirmationResponse> confirmPayment(@PathVariable String preBookingId) {
        try {
            PreBooking preBooking = preBookingService.getById(preBookingId);

            if (!preBooking.isConfirmed()) {
                Booking booking = bookingService.createBookingFromPreBooking(preBooking);
                preBookingService.markAsConfirmed(preBookingId, booking.getBookingNumber());
                preBooking = preBookingService.getById(preBookingId);
            }

            String summary = String.format(
                    "Prenotazione confermata per %s %s. Check-in: %s. Check-out: %s. Numero prenotazione: %s.",
                    preBooking.getFirstName(),
                    preBooking.getLastName(),
                    formatDate(preBooking.getCheckIn()),
                    formatDate(preBooking.getCheckOut()),
                    preBooking.getBookingNumber()
            );

            return ResponseEntity.ok(new CheckoutConfirmationResponse(
                    preBooking.getStatus() != null ? preBooking.getStatus().name() : "CONFIRMED",
                    preBooking.getPreBookingId(),
                    preBooking.getBookingNumber(),
                    preBooking.getTotalPriceEur(),
                    preBooking.getCheckIn(),
                    preBooking.getCheckOut(),
                    preBooking.getConfirmedAt(),
                    summary
            ));
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage(), exception);
        }
    }

    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(SUMMARY_DATE_FORMAT);
    }
}
