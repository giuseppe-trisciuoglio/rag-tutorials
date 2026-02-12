package com.github.rag.tutorials.chatbot.ai;

import com.github.rag.tutorials.chatbot.model.Booking;
import com.github.rag.tutorials.chatbot.model.PreBooking;
import com.github.rag.tutorials.chatbot.dto.PreBookingCheckoutLink;
import com.github.rag.tutorials.chatbot.service.BookingService;
import com.github.rag.tutorials.chatbot.service.PreBookingService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class BookingTools {
    private static final int DEMO_FIXED_BOOKING_PRICE_EUR = 1983;
    private static final Pattern SIMPLE_EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final BookingService bookingService;
    private final PreBookingService preBookingService;

    @Tool
    public Booking getBookingDetails(@P("booking number") String bookingNumber,
                                     @P("guest first name") String firstName,
                                     @P("guest last name") String lastName) {
        return bookingService.getBookingDetails(bookingNumber, firstName, lastName);
    }

    @Tool
    public void cancelBooking(@P("booking number") String bookingNumber,
                              @P("guest first name") String firstName,
                              @P("guest last name") String lastName) {
        bookingService.cancelBooking(bookingNumber, firstName, lastName);
    }
    
    @Tool
    public Booking createBooking(@P("guest first name") String firstName,
                                 @P("guest last name") String lastName,
                                 @P("guest phone number") String phoneNumber,
                                 @P("guest email") String email,
                                 @P("booking date and time") LocalDateTime bookingTime) {
        return bookingService.createBooking(firstName, lastName, phoneNumber, email, bookingTime);
    }

    @Tool
    public PreBookingCheckoutLink createPreBooking(@P("guest first name") String firstName,
                                                   @P("guest last name") String lastName,
                                                   @P("guest phone number") String phoneNumber,
                                                   @P("guest email") String email,
                                                   @P("check-in date and time") LocalDateTime checkIn,
                                                   @P("check-out date and time") LocalDateTime checkOut,
                                                   @P("total booking price in EUR, numeric value only") Integer totalPriceEur) {
        List<String> missingFields = new ArrayList<>();
        if (isBlank(firstName)) {
            missingFields.add("first name");
        }
        if (isBlank(lastName)) {
            missingFields.add("last name");
        }
        if (isBlank(phoneNumber)) {
            missingFields.add("phone number");
        }
        if (isBlank(email)) {
            missingFields.add("email");
        }
        if (checkIn == null) {
            missingFields.add("check-in date and time");
        }
        if (checkOut == null) {
            missingFields.add("check-out date and time");
        }

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing mandatory guest data for pre-booking: " + String.join(", ", missingFields)
                            + ". Ask the user for missing data and retry."
            );
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Invalid stay dates: check-out must be after check-in. Ask the user to correct dates and retry."
            );
        }

        if (!SIMPLE_EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException(
                    "Invalid guest email format. Ask the user for a valid email and retry."
            );
        }

        PreBooking preBooking = preBookingService.createPreBooking(
                firstName.trim(),
                lastName.trim(),
                phoneNumber.trim(),
                email.trim(),
                checkIn,
                checkOut,
                DEMO_FIXED_BOOKING_PRICE_EUR
        );
        String checkoutUrl = String.format(
                "http://localhost:8080/checkout.html?preBookingId=%s",
                preBooking.getPreBookingId()
        );
        return new PreBookingCheckoutLink(preBooking.getPreBookingId(), preBooking.getTotalPriceEur(), checkoutUrl);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
