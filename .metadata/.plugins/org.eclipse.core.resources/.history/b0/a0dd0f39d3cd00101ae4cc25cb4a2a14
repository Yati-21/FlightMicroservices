package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.request.BookingRequest;
import com.booking.service.service.BookingService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createBooking(@RequestBody @Valid BookingRequest request) {
        return service.bookTicket(request);
    }

    @GetMapping("/get/{pnr}")
    public Mono<Booking> getBooking(@PathVariable String pnr) {
        return service.getTicket(pnr);
    }

    @DeleteMapping("/cancel/{pnr}")
    public Mono<Void> cancelBooking(@PathVariable String pnr) {
        return service.cancelBooking(pnr);
    }

    @GetMapping("/history/user/{userId}")
    public Flux<Booking> getUserBookings(@PathVariable String userId) {
        return service.getBookingHistoryByUserId(userId);
    }

    @GetMapping("/history/email/{email}")
    public Flux<Booking> getHistoryByEmail(@PathVariable String email) {
        return service.getBookingHistoryByEmail(email);
    }
}
