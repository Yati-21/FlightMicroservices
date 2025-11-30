package com.airline.service.controller;


import com.airline.service.entity.Airline;
import com.airline.service.exception.NotFoundException;
import com.airline.service.repository.AirlineRepository;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/airlines")
public class AirlineController {

    private static final String AIRLINE_NOT_FOUND = "Airline not found";

    private final AirlineRepository airlineRepo;

    public AirlineController(AirlineRepository airlineRepo) {
        this.airlineRepo = airlineRepo;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<String> createAirline(@RequestBody @Valid AirlineCreateRequest req) {
        Airline airline = new Airline();
        airline.setCode(req.getCode());
        airline.setName(req.getName());
        return airlineRepo.save(airline).map(Airline::getCode);
    }

    @GetMapping
    public Flux<Airline> getAllAirlines() {
        return airlineRepo.findAll();
    }

    @GetMapping("/{code}")
    public Mono<Airline> getAirlineByCode(@PathVariable String code) {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException(AIRLINE_NOT_FOUND)));
    }

    @PutMapping("/{code}")
    public Mono<Airline> updateAirline(@PathVariable String code,
                                       @RequestBody @Valid AirlineUpdateRequest req) {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException(AIRLINE_NOT_FOUND)))
                .flatMap(existing -> {
                    existing.setName(req.getName());
                    return airlineRepo.save(existing);
                });
    }

    @DeleteMapping("/{code}")
    public Mono<Void> deleteAirline(@PathVariable String code) {
        return airlineRepo.findById(code)
                .switchIfEmpty(Mono.error(new NotFoundException(AIRLINE_NOT_FOUND)))
                .flatMap(existing -> airlineRepo.deleteById(code));
    }
}
