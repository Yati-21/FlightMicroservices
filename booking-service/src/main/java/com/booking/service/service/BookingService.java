package com.booking.service.service;

import com.booking.service.entity.Booking;
import com.booking.service.request.BookingRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {

    Mono<String> bookTicket(BookingRequest request);

    Mono<Booking> getTicket(String pnr);

    Flux<Booking> getBookingHistoryByUserId(String userId);

    Flux<Booking> getBookingHistoryByEmail(String email);

    Mono<Void> cancelBooking(String pnr);
}
