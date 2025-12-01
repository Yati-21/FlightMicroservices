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

		BookingRequest bookingReq = new BookingRequest();
		bookingReq.setFlightId("F100");
		bookingReq.setUserId("U200");
		bookingReq.setSeatsBooked(1);
		bookingReq.setMealType(MEAL_TYPE.VEG);
		bookingReq.setFlightType(FLIGHT_TYPE.ONE_WAY);

		PassengerRequest passengerReq1 = new PassengerRequest();
		passengerReq1.setName("abc");
		passengerReq1.setAge(25);
		passengerReq1.setGender(GENDER.M);
		passengerReq1.setSeatNumber("A1");

		bookingReq.setPassengers(List.of(passengerReq1));

		Mockito.when(bookingService.bookTicket(Mockito.any())).thenReturn(Mono.just("PNR123"));

		webTestClient.post().uri("/bookings/create").contentType(MediaType.APPLICATION_JSON).bodyValue(bookingReq)
				.exchange().expectStatus().isCreated().expectBody(String.class).isEqualTo("PNR123");
	}

	@Test
	void testGetBooking() {
		Booking booking = new Booking();
		booking.setPnr("PNR123");

		Mockito.when(bookingService.getTicket("PNR123")).thenReturn(Mono.just(booking));

		webTestClient.get().uri("/bookings/get/PNR123").exchange().expectStatus().isOk().expectBody().jsonPath("$.pnr")
				.isEqualTo("PNR123");
	}

	@Test
	void testCancelBooking() {
		Mockito.when(bookingService.cancelBooking("PNR123")).thenReturn(Mono.empty());

		webTestClient.delete().uri("/bookings/cancel/PNR123").exchange().expectStatus().isOk();
	}

	@Test
	void testHistoryByUser() {
		Booking booking = new Booking();
		booking.setUserId("U1");

		Mockito.when(bookingService.getBookingHistoryByUserId("U1")).thenReturn(Flux.just(booking));

		webTestClient.get().uri("/bookings/history/user/U1").exchange().expectStatus().isOk().expectBody()
				.jsonPath("$[0].userId").isEqualTo("U1");
	}
}
