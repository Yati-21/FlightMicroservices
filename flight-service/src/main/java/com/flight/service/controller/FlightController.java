package com.flight.service.controller;

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
@RequestMapping("/flights")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> addFlight(@RequestBody @Valid Flight flight) {
        return service.addFlight(flight).map(Flight::getId);
    }

    @PostMapping("/search")
    public Flux<Flight> search(@RequestBody @Valid FlightSearchRequest req) {
        return service.searchFlights(
                req.getFromCity(),
                req.getToCity(),
                req.getDate()
        );
    }

    @GetMapping("/get/{flightId}")
    public Mono<Flight> getFlight(@PathVariable String flightId) {
    	log.info("......Booking-Service CALLING Flight-Service for flightId=" + flightId);
    	return service.getFlightById(flightId);
    }

    @GetMapping("/airline/{code}")
    public Flux<Flight> getFlightsByAirline(@PathVariable String code) {
        return service.getFlightsByAirline(code);
    }
}
