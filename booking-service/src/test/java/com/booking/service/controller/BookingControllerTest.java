package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.entity.FLIGHT_TYPE;
import com.booking.service.entity.GENDER;
import com.booking.service.entity.MEAL_TYPE;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;
import com.booking.service.service.BookingService;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookingService bookingService;


    @Test
    void testCreateBooking() {

        BookingRequest req = new BookingRequest();
        req.setFlightId("F100");
        req.setUserId("U200");
        req.setSeatsBooked(1);
        req.setMealType(MEAL_TYPE.VEG);
        req.setFlightType(FLIGHT_TYPE.ONE_WAY);

        PassengerRequest p1 = new PassengerRequest();
        p1.setName("John");
        p1.setAge(25);
        p1.setGender(GENDER.M);
        p1.setSeatNumber("A1");

        req.setPassengers(List.of(p1));

        Mockito.when(bookingService.bookTicket(Mockito.any()))
                .thenReturn(Mono.just("PNR123"));

        webTestClient.post()
                .uri("/bookings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("PNR123");
    }


    @Test
    void testGetBooking() {
        Booking b = new Booking();
        b.setPnr("PNR123");

        Mockito.when(bookingService.getTicket("PNR123"))
                .thenReturn(Mono.just(b));

        webTestClient.get()
                .uri("/bookings/get/PNR123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.pnr").isEqualTo("PNR123");
    }

    @Test
    void testCancelBooking() {
        Mockito.when(bookingService.cancelBooking("PNR123"))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/bookings/cancel/PNR123")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testHistoryByUser() {
        Booking b = new Booking();
        b.setUserId("U1");

        Mockito.when(bookingService.getBookingHistoryByUserId("U1"))
                .thenReturn(Flux.just(b));

        webTestClient.get()
                .uri("/bookings/history/user/U1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].userId").isEqualTo("U1");
    }
}
