package com.flight.service.controller;

import com.flight.service.entity.*;
import com.flight.service.request.FlightSearchRequest;
import com.flight.service.service.FlightService;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

@WebFluxTest(controllers = FlightController.class)
class FlightControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private FlightService flightService;

	private Flight flight;

	@BeforeEach
	void setup() {
		flight = new Flight();
		flight.setId("F1");
		flight.setAirlineCode("AI");
		flight.setFlightNumber("AI101");
		flight.setFromCity(AIRPORT_CODE.DEL);
		flight.setToCity(AIRPORT_CODE.BOM);
		flight.setDepartureTime(LocalDateTime.now().plusDays(1));
		flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
		flight.setTotalSeats(180);
		flight.setAvailableSeats(180);
		flight.setPrice(5500);
		flight.setStatus(FLIGHT_STATUS.SCHEDULED);
	}

	@Test
	void addFlight_success() {
		Mockito.when(flightService.addFlight(Mockito.any())).thenReturn(Mono.just(flight));
		webTestClient.post().uri("/flights/add").contentType(MediaType.APPLICATION_JSON).bodyValue(flight).exchange()
				.expectStatus().isCreated().expectBody(String.class).isEqualTo("F1");
	}

	@Test
	void searchFlights_success() {
		FlightSearchRequest flightSearchReq = new FlightSearchRequest();
		flightSearchReq.setFromCity(AIRPORT_CODE.DEL);
		flightSearchReq.setToCity(AIRPORT_CODE.BOM);
		flightSearchReq.setDate(LocalDate.now().plusDays(1));

		Mockito.when(flightService.searchFlights(AIRPORT_CODE.DEL, AIRPORT_CODE.BOM, flightSearchReq.getDate()))
				.thenReturn(Flux.just(flight));
		webTestClient.post().uri("/flights/search").contentType(MediaType.APPLICATION_JSON).bodyValue(flightSearchReq)
				.exchange().expectStatus().isOk().expectBody().jsonPath("$[0].id").isEqualTo("F1")
				.jsonPath("$[0].airlineCode").isEqualTo("AI").jsonPath("$[0].fromCity").isEqualTo("DEL")
				.jsonPath("$[0].toCity").isEqualTo("BOM");
	}

	@Test
	void getFlight_success() {
		Mockito.when(flightService.getFlightById("F1")).thenReturn(Mono.just(flight));
		webTestClient.get().uri("/flights/get/F1").exchange().expectStatus().isOk().expectBody().jsonPath("$.id")
				.isEqualTo("F1");
	}

	@Test
	void getFlightsByAirline_success() {
		Mockito.when(flightService.getFlightsByAirline("AI")).thenReturn(Flux.just(flight));
		webTestClient.get().uri("/flights/airline/AI").exchange().expectStatus().isOk().expectBody()
				.jsonPath("$[0].flightNumber").isEqualTo("AI101");
	}
}
