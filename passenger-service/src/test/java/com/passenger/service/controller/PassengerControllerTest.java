package com.passenger.service.controller;

import com.passenger.service.entity.GENDER;
import com.passenger.service.request.PassengerCreateRequest;
import com.passenger.service.service.PassengerService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.passenger.service.entity.Passenger;

@WebFluxTest(PassengerController.class)
public class PassengerControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private PassengerService service;

    @Test
    void testCreatePassenger() {
        PassengerCreateRequest req = new PassengerCreateRequest();
        req.setName("Aman");
        req.setGender(GENDER.M);
        req.setAge(25);
        req.setSeatNumber("A1");
        req.setBookingId("B1");

        Mockito.when(service.createPassenger(req)).thenReturn(Mono.just("P123"));

        webClient.post().uri("/passengers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("P123");
    }

    @Test
    void testGetByBookingId() {
        Passenger p = new Passenger();
        p.setId("P1");
        p.setName("Aman");

        Mockito.when(service.getByBookingId("B1"))
                .thenReturn(Flux.just(p));

        webClient.get().uri("/passengers/booking/B1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Passenger.class)
                .hasSize(1);
    }

    @Test
    void testDeleteByBooking() {
        Mockito.when(service.deleteByBookingId("B1"))
                .thenReturn(Mono.empty());

        webClient.delete().uri("/passengers/booking/B1")
                .exchange()
                .expectStatus().isOk();
    }
}
