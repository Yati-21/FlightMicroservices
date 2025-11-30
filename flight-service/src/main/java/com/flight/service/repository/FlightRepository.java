package com.flight.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.flight.service.entity.AIRPORT_CODE;
import com.flight.service.entity.Flight;

import reactor.core.publisher.Flux;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {

    Flux<Flight> findByFromCityAndToCity(AIRPORT_CODE from, AIRPORT_CODE to);

    Flux<Flight> findByAirlineCode(String airlineCode);
}
