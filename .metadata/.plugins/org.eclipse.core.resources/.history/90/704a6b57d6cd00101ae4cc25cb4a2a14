package com.passenger.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.passenger.service.entity.Passenger;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {

    Flux<Passenger> findByBookingId(String bookingId);

    Mono<Void> deleteByBookingId(String bookingId);
}
