package com.airline.service.service;

import com.airline.service.entity.Airline;
import com.airline.service.exception.NotFoundException;
import com.airline.service.repository.AirlineRepository;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class AirlineServiceImplTest {

	private final AirlineRepository repo = Mockito.mock(AirlineRepository.class);

	private final AirlineServiceImpl service = new AirlineServiceImpl(repo);

	@Test
	void testCreateAirline() {
		AirlineCreateRequest req = new AirlineCreateRequest();
		req.setCode("AI");
		req.setName("Air India");

		Airline saved = new Airline("AI", "Air India");

		Mockito.when(repo.save(saved)).thenReturn(Mono.just(saved));

		StepVerifier.create(service.createAirline(req)).expectNextMatches(a -> a.getCode().equals("AI"))
				.verifyComplete();
	}

	@Test
	void testGetAirlineSuccess() {
		Airline airline = new Airline("AI", "Air India");

		Mockito.when(repo.findById("AI")).thenReturn(Mono.just(airline));

		StepVerifier.create(service.getAirline("AI")).expectNext(airline).verifyComplete();
	}

	@Test
	void testGetAirlineNotFound() {
		Mockito.when(repo.findById("ZZ")).thenReturn(Mono.empty());

		StepVerifier.create(service.getAirline("ZZ")).expectError(NotFoundException.class).verify();
	}

	@Test
	void testGetAllAirlines() {
		Airline a1 = new Airline("AI", "Air India");
		Airline a2 = new Airline("EM", "Emirates");

		Mockito.when(repo.findAll()).thenReturn(Flux.just(a1, a2));

		StepVerifier.create(service.getAllAirlines()).expectNext(a1).expectNext(a2).verifyComplete();
	}

	@Test
	void testUpdateAirlineSuccess() {
		Airline existing = new Airline("AI", "Old Name");

		Mockito.when(repo.findById("AI")).thenReturn(Mono.just(existing));

		AirlineUpdateRequest req = new AirlineUpdateRequest();
		req.setName("New Name");

		Airline updated = new Airline("AI", "New Name");
		Mockito.when(repo.save(existing)).thenReturn(Mono.just(updated));

		StepVerifier.create(service.updateAirline("AI", req)).expectNextMatches(a -> a.getName().equals("New Name"))
				.verifyComplete();
	}

	@Test
	void testUpdateAirlineNotFound() {
		Mockito.when(repo.findById("NA")).thenReturn(Mono.empty());

		AirlineUpdateRequest req = new AirlineUpdateRequest();
		req.setName("Any");

		StepVerifier.create(service.updateAirline("NA", req)).expectError(NotFoundException.class).verify();
	}

	@Test
	void testDeleteAirlineSuccess() {
		Airline existing = new Airline("AI", "Air India");

		Mockito.when(repo.findById("AI")).thenReturn(Mono.just(existing));
		Mockito.when(repo.delete(existing)).thenReturn(Mono.empty());

		StepVerifier.create(service.deleteAirline("AI")).verifyComplete();
	}

	@Test
	void testDeleteAirlineNotFound() {
		Mockito.when(repo.findById("XX")).thenReturn(Mono.empty());

		StepVerifier.create(service.deleteAirline("XX")).expectError(NotFoundException.class).verify();
	}
}
