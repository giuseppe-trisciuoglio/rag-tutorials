package com.github.rag.tutorials.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "booking")
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    @Column(name = "pre_booking_id", unique = true)
    private String preBookingId;
    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;
    @Column(name = "check_in")
    private LocalDateTime checkIn;
    @Column(name = "check_out")
    private LocalDateTime checkOut;
    @Column(name = "total_price_eur")
    private Integer totalPriceEur;
    @Column(name = "booking_number", nullable = false)
    private String bookingNumber;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "email")
    private String email;

    public static Booking fromPreBooking(PreBooking preBooking) {
        Booking booking = new Booking();
        booking.setPreBookingId(preBooking.getPreBookingId());
        booking.setFirstName(preBooking.getFirstName());
        booking.setLastName(preBooking.getLastName());
        booking.setPhoneNumber(preBooking.getPhoneNumber());
        booking.setEmail(preBooking.getEmail());
        booking.setBookingDate(preBooking.getBookingDate() != null ? preBooking.getBookingDate() : preBooking.getCheckIn());
        booking.setCheckIn(preBooking.getCheckIn());
        booking.setCheckOut(preBooking.getCheckOut());
        booking.setTotalPriceEur(preBooking.getTotalPriceEur());
        return booking;
    }
    
    @PrePersist
    private void ensureBookingNumber() {
        if (bookingNumber == null) {
            bookingNumber = String.format("B%04d", UUID.randomUUID().hashCode() & Integer.MAX_VALUE);
        }
    }
}
