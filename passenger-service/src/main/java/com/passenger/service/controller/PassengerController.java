package com.passenger.service.controller;

import com.passenger.service.entity.Passenger;
import com.passenger.service.request.PassengerCreateRequest;
import com.passenger.service.service.PassengerService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService service;

    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createPassenger(@RequestBody @Valid PassengerCreateRequest req) {
        return service.createPassenger(req);
    }

    @GetMapping("/booking/{bookingId}")
    public Flux<Passenger> getPassengersByBooking(@PathVariable String bookingId) {
        return service.getByBookingId(bookingId);
    }

    @DeleteMapping("/booking/{bookingId}")
    public Mono<Void> deleteByBooking(@PathVariable String bookingId) {
        return service.deleteByBookingId(bookingId);
    }
}
