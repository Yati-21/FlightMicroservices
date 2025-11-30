package com.passenger.service.service;

import com.passenger.service.entity.GENDER;
import com.passenger.service.entity.Passenger;
import com.passenger.service.repository.PassengerRepository;
import com.passenger.service.request.PassengerCreateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

public class PassengerServiceImplTest {

    @Mock
    private PassengerRepository repo;

    @InjectMocks
    private PassengerServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePassenger() {
        PassengerCreateRequest req = new PassengerCreateRequest();
        req.setName("Aman");
        req.setGender(GENDER.M);
        req.setAge(25);
        req.setSeatNumber("A1");
        req.setBookingId("B1");

        Passenger saved = new Passenger();
        saved.setId("P1");
        saved.setName("Aman");

        when(repo.save(any(Passenger.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(service.createPassenger(req))
                .expectNext("P1")
                .verifyComplete();
    }

    @Test
    void testGetByBookingId() {
        Passenger p = new Passenger();
        p.setId("P1");

        when(repo.findByBookingId("B1")).thenReturn(Flux.just(p));

        StepVerifier.create(service.getByBookingId("B1"))
                .expectNext(p)
                .verifyComplete();
    }

    @Test
    void testDeleteByBookingId() {
        Passenger p = new Passenger();
        p.setId("P1");

        when(repo.findByBookingId("B1")).thenReturn(Flux.just(p));
        when(repo.deleteById("P1")).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteByBookingId("B1"))
                .verifyComplete();
    }
}
