package com.flight.service.controller;

import com.flight.service.entity.AIRPORT_CODE;
import com.flight.service.entity.Flight;
import com.flight.service.request.FlightSearchRequest;
import com.flight.service.service.FlightService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping("/flights/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> addFlight(@RequestBody @Valid Flight flight) {
        return service.addFlight(flight).map(Flight::getId);
    }

    @PostMapping("/flights/search")
    public Flux<String> searchFlights(@RequestBody @Valid FlightSearchRequest request) {
        AIRPORT_CODE from = request.getFrom();
        AIRPORT_CODE to = request.getTo();
        return service.searchFlights(from, to, request.getDate())
                .map(Flight::getId);
    }

    @GetMapping("/flights/get/{flightId}")
    public Mono<Flight> getFlight(@PathVariable String flightId) {
        return service.getFlightById(flightId);
    }

    @GetMapping("/airlines/{code}/flights")
    public Flux<Flight> getFlightsByAirline(@PathVariable String code) {
        return service.getFlightsByAirline(code);
    }
}
