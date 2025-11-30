package com.flight.service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.flight.service.client.AirlineClient;
import com.flight.service.dto.AirlineDto;
import com.flight.service.entity.AIRPORT_CODE;
import com.flight.service.entity.FLIGHT_STATUS;
import com.flight.service.entity.Flight;
import com.flight.service.exception.BusinessException;
import com.flight.service.exception.NotFoundException;
import com.flight.service.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepo;

    @Mock
    private AirlineClient airlineClient;

    @InjectMocks
    private FlightServiceImpl service;

    private Flight validFlight;
    private AirlineDto validAirline;

    @BeforeEach
    void setup() {
        LocalDateTime now = LocalDateTime.now().plusDays(2);

        validFlight = new Flight();
        validFlight.setId("F1");
        validFlight.setAirlineCode("AI");
        validFlight.setFlightNumber("AI100");
        validFlight.setFromCity(AIRPORT_CODE.DEL);
        validFlight.setToCity(AIRPORT_CODE.BOM);
        validFlight.setDepartureTime(now);
        validFlight.setArrivalTime(now.plusHours(2));
        validFlight.setTotalSeats(100);
        validFlight.setAvailableSeats(100);
        validFlight.setPrice(5000);
        validFlight.setStatus(FLIGHT_STATUS.SCHEDULED);

        validAirline = new AirlineDto();
        validAirline.setCode("AI");
        validAirline.setName("Air India");
    }

    @Test
    void addFlight_success() {
        when(airlineClient.getAirline("AI")).thenReturn(validAirline);
        when(flightRepo.save(any(Flight.class))).thenReturn(Mono.just(validFlight));

        StepVerifier.create(service.addFlight(validFlight))
                .expectNextMatches(f -> f.getId().equals("F1") && f.getAvailableSeats() == f.getTotalSeats())
                .verifyComplete();
    }

    @Test
    void addFlight_airlineNotFound() {
        AirlineDto empty = new AirlineDto(); // code = null
        when(airlineClient.getAirline("AI")).thenReturn(empty);

        StepVerifier.create(service.addFlight(validFlight))
                .expectErrorMatches(ex -> ex instanceof NotFoundException &&
                        ex.getMessage().equals("Airline not found"))
                .verify();
    }

    @Test
    void addFlight_sameSourceDestination_error() {
        when(airlineClient.getAirline("AI")).thenReturn(validAirline);

        validFlight.setToCity(AIRPORT_CODE.DEL);

        StepVerifier.create(service.addFlight(validFlight))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ex.getMessage().contains("source and destination must be different"))
                .verify();
    }

    @Test
    void searchFlights_success() {
        validFlight.setDepartureTime(LocalDateTime.of(2030, 1, 1, 10, 0));
        LocalDate date = LocalDate.of(2030, 1, 1);

        when(flightRepo.findByFromCityAndToCity(AIRPORT_CODE.DEL, AIRPORT_CODE.BOM))
                .thenReturn(Flux.just(validFlight));

        StepVerifier.create(service.searchFlights(AIRPORT_CODE.DEL, AIRPORT_CODE.BOM, date))
                .expectNext(validFlight)
                .verifyComplete();
    }

    @Test
    void getFlightById_success() {
        when(flightRepo.findById("F1")).thenReturn(Mono.just(validFlight));

        StepVerifier.create(service.getFlightById("F1"))
                .expectNext(validFlight)
                .verifyComplete();
    }

    @Test
    void getFlightById_notFound() {
        when(flightRepo.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(service.getFlightById("F1"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void getFlightsByAirline_success() {
        when(flightRepo.findByAirlineCode("AI")).thenReturn(Flux.just(validFlight));

        StepVerifier.create(service.getFlightsByAirline("AI"))
                .expectNext(validFlight)
                .verifyComplete();
    }
}
