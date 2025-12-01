package com.booking.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.booking.service.entity.Passenger;

import reactor.core.publisher.Flux;

@Repository
public interface PassengerRepository extends ReactiveMongoRepository<Passenger, String> {

	Flux<Passenger> findByBookingId(String bookingId);
}
