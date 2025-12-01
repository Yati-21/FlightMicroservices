package com.passenger.service.controller;

import com.passenger.service.request.PassengerCreateRequest;
import com.passenger.service.service.PassengerService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/passengers")
public class PassengerInternalController {

    private final PassengerService service;

    public PassengerInternalController(PassengerService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<String> createPassengerInternal(
            @RequestHeader(value = "X-Internal-Call", required = false) String internal,
            @RequestBody @Valid PassengerCreateRequest req) {

        if (!"booking-service".equals(internal)) {
            return Mono.error(new RuntimeException("Unauthorized access"));
        }

        return service.createPassenger(req);
    }

}
