package com.airline.service.controller;

import com.airline.service.entity.Airline;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;
import com.airline.service.service.AirlineService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = AirlineController.class)
public class AirlineControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private AirlineService service;

	@Test
	void testCreateAirline() {
		AirlineCreateRequest req = new AirlineCreateRequest();
		req.setCode("AI");
		req.setName("Air India");

		Airline saved = new Airline("AI", "Air India");

		Mockito.when(service.createAirline(req)).thenReturn(Mono.just(saved));

		webTestClient.post().uri("/airlines").bodyValue(req).exchange().expectStatus().isCreated().expectBody()
				.jsonPath("$.code").isEqualTo("AI").jsonPath("$.name").isEqualTo("Air India");
	}

	@Test
	void testGetAirline() {
		Airline airline = new Airline("AI", "Air India");

		Mockito.when(service.getAirline("AI")).thenReturn(Mono.just(airline));

		webTestClient.get().uri("/airlines/AI").exchange().expectStatus().isOk().expectBody().jsonPath("$.name")
				.isEqualTo("Air India");
	}

	@Test
	void testGetAllAirlines() {
		Airline a1 = new Airline("AI", "Air India");
		Airline a2 = new Airline("EM", "Emirates");

		Mockito.when(service.getAllAirlines()).thenReturn(Flux.just(a1, a2));

		webTestClient.get().uri("/airlines").exchange().expectStatus().isOk().expectBody().jsonPath("$[0].code")
				.isEqualTo("AI").jsonPath("$[1].code").isEqualTo("EM");
	}

	@Test
	void testUpdateAirline() {
		AirlineUpdateRequest req = new AirlineUpdateRequest();
		req.setName("New Name");

		Airline updated = new Airline("AI", "New Name");

		Mockito.when(service.updateAirline("AI", req)).thenReturn(Mono.just(updated));

		webTestClient.put().uri("/airlines/AI").bodyValue(req).exchange().expectStatus().isOk().expectBody()
				.jsonPath("$.name").isEqualTo("New Name");
	}

	@Test
	void testDeleteAirline() {
		Mockito.when(service.deleteAirline("AI")).thenReturn(Mono.empty());

		webTestClient.delete().uri("/airlines/AI").exchange().expectStatus().isOk();
	}
}
