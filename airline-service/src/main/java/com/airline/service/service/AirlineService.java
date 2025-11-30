package com.airline.service.service;

import com.airline.service.entity.Airline;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AirlineService {

    Mono<Airline> createAirline(AirlineCreateRequest req);

    Mono<Airline> getAirline(String code);

    Flux<Airline> getAllAirlines();

    Mono<Airline> updateAirline(String code, AirlineUpdateRequest req);

    Mono<Void> deleteAirline(String code);
}
