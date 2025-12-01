package com.booking.service.controller;

import com.booking.service.entity.Booking;
import com.booking.service.entity.FLIGHT_TYPE;
import com.booking.service.entity.GENDER;
import com.booking.service.entity.MEAL_TYPE;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;
import com.booking.service.service.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest(BookingController.class)
class BookingControllerTest {

	@Autowired
	private WebTestClient webClient;

	@MockitoBean
	private BookingService service;

	private BookingRequest bookingReq;

	@BeforeEach
	void setup() {
		PassengerRequest p = new PassengerRequest();
		p.setName("Aman");
		p.setGender(GENDER.M);
		p.setAge(20);
		p.setSeatNumber("A1");

		bookingReq = new BookingRequest();
		bookingReq.setFlightId("F1");
		bookingReq.setUserId("U1");
		bookingReq.setSeatsBooked(1);
		bookingReq.setMealType(MEAL_TYPE.VEG);
		bookingReq.setFlightType(FLIGHT_TYPE.ONE_WAY);
		bookingReq.setPassengers(List.of(p));
	}

	@Test
	void testCreateBooking() {
		Mockito.when(service.bookTicket(Mockito.any())).thenReturn(Mono.just("PNR1234"));

		webClient.post().uri("/bookings/create").contentType(MediaType.APPLICATION_JSON).bodyValue(bookingReq)
				.exchange().expectStatus().isCreated().expectBody(String.class).isEqualTo("PNR1234");
	}

	@Test
	void testGetBooking() {
		Booking b = new Booking();
		b.setPnr("PNR123");

		Mockito.when(service.getTicket("PNR123")).thenReturn(Mono.just(b));

		webClient.get().uri("/bookings/get/PNR123").exchange().expectStatus().isOk().expectBody().jsonPath("$.pnr")
				.isEqualTo("PNR123");
	}

	@Test
	void testCancelBooking() {
		Mockito.when(service.cancelBooking("PNR1")).thenReturn(Mono.empty());

		webClient.delete().uri("/bookings/cancel/PNR1").exchange().expectStatus().isOk();
	}

	@Test
	void testGetHistoryByUser() {
		Booking b = new Booking();
		b.setId("B1");

		Mockito.when(service.getBookingHistoryByUserId("U1")).thenReturn(Flux.just(b));

		webClient.get().uri("/bookings/history/user/U1").exchange().expectStatus().isOk().expectBody()
				.jsonPath("$[0].id").isEqualTo("B1");
	}
}
