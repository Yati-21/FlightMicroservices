package com.airline.service.controller;

import com.airline.service.entity.Airline;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;
import com.airline.service.service.AirlineService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/airlines")
public class AirlineController {

    private final AirlineService service;

    public AirlineController(AirlineService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Airline> createAirline(@RequestBody @Valid AirlineCreateRequest req) {
        return service.createAirline(req);
    }

    @GetMapping
    public Flux<Airline> getAllAirlines() {
        return service.getAllAirlines();
    }

    @GetMapping("/{code}")
    public Mono<Airline> getAirline(@PathVariable String code) {
        return service.getAirline(code);
    }

    @PutMapping("/{code}")
    public Mono<Airline> updateAirline(
            @PathVariable String code,
            @RequestBody @Valid AirlineUpdateRequest req) {
        return service.updateAirline(code, req);
    }

    @DeleteMapping("/{code}")
    public Mono<Void> deleteAirline(@PathVariable String code) {
        return service.deleteAirline(code);
    }
}
