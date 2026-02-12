package com.github.rag.tutorials.chatbot.service;

import com.github.rag.tutorials.chatbot.model.Booking;
import com.github.rag.tutorials.chatbot.model.PreBooking;
import com.github.rag.tutorials.chatbot.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;

    public Booking getBookingDetails(String bookingNumber, String firstName, String lastName) {
        ensureExists(bookingNumber, firstName, lastName);
        return bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new BookingNotFoundException(bookingNumber));
    }

    public void cancelBooking(String bookingNumber, String firstName, String lastName) {
        ensureExists(bookingNumber, firstName, lastName);
    }

    private void ensureExists(String bookingNumber, String firstName, String lastName) {
        Booking booking = bookingRepository.findByBookingNumber(bookingNumber)
                .orElseThrow(() -> new BookingNotFoundException(bookingNumber));
        if (booking == null) {
            throw new BookingNotFoundException(bookingNumber);
        }

        if (!booking.getFirstName().equals(firstName)) {
            throw new BookingNotFoundException(bookingNumber);
        }
        if (!booking.getLastName().equals(lastName)) {
            throw new BookingNotFoundException(bookingNumber);
        }
    }

    public Booking createBooking(String firstName, String lastName, String phoneNumber, String email, LocalDateTime bookingDate) {
        boolean exist = bookingRepository.existsByBookingDateAndFirstNameIgnoreCaseAndLastNameIgnoreCase(
                bookingDate,
                firstName,
                lastName
        );
        if (exist) {
            throw new BookingExistsException(bookingDate);
        }
        Booking booking = new Booking();
        booking.setFirstName(firstName);
        booking.setLastName(lastName);
        booking.setPhoneNumber(phoneNumber);
        booking.setEmail(email);
        booking.setBookingDate(bookingDate);
        return bookingRepository.save(booking);
    }

    public Booking createBookingFromPreBooking(PreBooking preBooking) {
        if (preBooking == null) {
            throw new IllegalArgumentException("Pre-booking cannot be null.");
        }
        if (preBooking.getPreBookingId() == null || preBooking.getPreBookingId().isBlank()) {
            throw new IllegalArgumentException("Pre-booking ID is required to confirm a booking.");
        }

        return bookingRepository.findByPreBookingId(preBooking.getPreBookingId())
                .orElseGet(() -> bookingRepository.save(Booking.fromPreBooking(preBooking)));
    }
}
