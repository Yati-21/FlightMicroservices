package com.airline.service.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import com.airline.service.entity.Airline;

@Repository
public interface AirlineRepository extends ReactiveMongoRepository<Airline, String> {
}
