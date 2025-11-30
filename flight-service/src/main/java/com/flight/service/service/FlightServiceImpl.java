package com.flight.service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.flight.service.client.AirlineClient;
import com.flight.service.dto.AirlineDto;
import com.flight.service.entity.AIRPORT_CODE;
import com.flight.service.entity.Flight;
import com.flight.service.exception.BusinessException;
import com.flight.service.exception.NotFoundException;
import com.flight.service.repository.FlightRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepo;
    private final AirlineClient airlineClient;

    public FlightServiceImpl(FlightRepository flightRepo, AirlineClient airlineClient) {
        this.flightRepo = flightRepo;
        this.airlineClient = airlineClient;
    }

    @Override
    @CircuitBreaker(name = "airlineServiceCB", fallbackMethod = "addFlightFallback")
    public Mono<Flight> addFlight(Flight flight) {

        // Call airline-service to validate airline exists (wrapped as reactive)
        return Mono.fromCallable(() -> airlineClient.getAirline(flight.getAirlineCode()))
                .subscribeOn(Schedulers.boundedElastic())
                // if Feign throws, convert to proper NotFoundException
                .onErrorResume(e -> Mono.error(new NotFoundException("Airline service unavailable")))
                .flatMap((AirlineDto airline) -> {

                    if (airline.getCode() == null) {
                        return Mono.error(new NotFoundException("Airline not found"));
                    }

                    // === SAME VALIDATIONS AS MONOLITHIC ===
                    if (flight.getFromCity().equals(flight.getToCity())) {
                        return Mono.error(new BusinessException("source and destination must be different"));
                    }
                    if (flight.getArrivalTime().isBefore(flight.getDepartureTime())) {
                        return Mono.error(new BusinessException("arrival time must be after departure time"));
                    }
                    if (flight.getDepartureTime().isBefore(LocalDateTime.now())) {
                        return Mono.error(new BusinessException("Flight departure time must be in the future"));
                    }

                    // initialize availableSeats = totalSeats
                    flight.setAvailableSeats(flight.getTotalSeats());

                    return flightRepo.save(flight);
                });
    }

    // Fallback when CircuitBreaker opens / airline service repeatedly fails
    @SuppressWarnings("unused")
    public Mono<Flight> addFlightFallback(Flight flight, Throwable ex) {
        return Mono.error(new BusinessException("Airline service DOWN. Cannot validate airline"));
    }

    @Override
    public Flux<Flight> searchFlights(AIRPORT_CODE from, AIRPORT_CODE to, LocalDate date) {
        return flightRepo.findByFromCityAndToCity(from, to)
                .filter(f -> f.getDepartureTime().toLocalDate().equals(date));
    }

    @Override
    public Mono<Flight> getFlightById(String flightId) {
        return flightRepo.findById(flightId)
                .switchIfEmpty(Mono.error(new NotFoundException("Flight not found")));
    }

    @Override
    public Flux<Flight> getFlightsByAirline(String airlineCode) {
        return flightRepo.findByAirlineCode(airlineCode);
    }
}
