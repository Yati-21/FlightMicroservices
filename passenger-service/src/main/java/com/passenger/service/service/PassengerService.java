package com.passenger.service.service;

import com.passenger.service.entity.Passenger;
import com.passenger.service.request.PassengerCreateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PassengerService {

    Mono<String> createPassenger(PassengerCreateRequest req);

    Flux<Passenger> getByBookingId(String bookingId);

    Mono<Void> deleteByBookingId(String bookingId);
}
