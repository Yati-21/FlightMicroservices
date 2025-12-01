package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.entity.FLIGHT_TYPE;
import com.booking.service.entity.GENDER;
import com.booking.service.entity.MEAL_TYPE;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;
import com.booking.service.service.BookingService;

import ch.qos.logback.core.net.server.Client;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.*;

@WebFluxTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookingService bookingService;

    private BookingRequest validReq;

    @BeforeEach
    void setup() {
        PassengerRequest p = new PassengerRequest();
        p.setName("John");
        p.setGender(GENDER.M);
        p.setAge(30);
        p.setSeatNumber("A1");

        validReq = new BookingRequest();
        validReq.setFlightId("F1");
        validReq.setUserId("U1");
        validReq.setSeatsBooked(1);
        validReq.setMealType(MEAL_TYPE.VEG);
        validReq.setFlightType(FLIGHT_TYPE.ONE_WAY);
        validReq.setPassengers(List.of(p));
    }

    @Test
    void testCreateBooking() {
        Mockito.when(bookingService.bookTicket(Mockito.any()))
                .thenReturn(Mono.just("PNR123"));

        webTestClient.post()
                .uri("/bookings/create")
                .bodyValue(validReq)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("PNR123");
    }

    @Test
    void testGetBooking() {
        Booking booking = new Booking();
        booking.setPnr("PNR1");

        Mockito.when(bookingService.getTicket("PNR1"))
                .thenReturn(Mono.just(booking));

        webTestClient.get()
                .uri("/bookings/get/PNR1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR1");
    }

    @Test
    void testCancelBooking() {
        Mockito.when(bookingService.cancelBooking("PNR1"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/bookings/cancel/PNR1")
                .exchange()
                .expectStatus().isOk();
    }
}

