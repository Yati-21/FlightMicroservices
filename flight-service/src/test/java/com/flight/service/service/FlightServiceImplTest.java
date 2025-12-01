package com.flight.service.service;

import com.flight.service.client.AirlineClient;
import com.flight.service.dto.AirlineDto;
import com.flight.service.entity.AIRPORT_CODE;
import com.flight.service.entity.Flight;
import com.flight.service.exception.BusinessException;
import com.flight.service.exception.NotFoundException;
import com.flight.service.repository.FlightRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class FlightServiceImplTest {

	private FlightRepository flightRepo;
	private AirlineClient airlineClient;

	private FlightServiceImpl service;

	@BeforeEach
	void setup() {
		flightRepo = mock(FlightRepository.class);
		airlineClient = mock(AirlineClient.class);
		service = new FlightServiceImpl(flightRepo, airlineClient);
	}

	private Flight makeValidFlight() {
		Flight flight = new Flight();
		flight.setAirlineCode("AI");
		flight.setFromCity(AIRPORT_CODE.DEL);
		flight.setToCity(AIRPORT_CODE.BOM);
		flight.setTotalSeats(150);
		flight.setDepartureTime(LocalDateTime.now().plusDays(1));
		flight.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(2));
		return flight;
	}

	@Test
	void testAddFlight_Success() {
		Flight flight = makeValidFlight();
		AirlineDto dto = new AirlineDto();
		dto.setCode("AI");
		when(airlineClient.getAirline("AI")).thenReturn(dto);
		when(flightRepo.save(flight)).thenReturn(Mono.just(flight));
		StepVerifier.create(service.addFlight(flight)).expectNext(flight).verifyComplete();
	}

	@Test
	void testAddFlight_AirlineServiceError() {
		Flight flight = makeValidFlight();
		when(airlineClient.getAirline("AI")).thenThrow(new RuntimeException("down"));
		StepVerifier.create(service.addFlight(flight)).expectError(NotFoundException.class).verify();
	}

	@Test
	void testAddFlight_AirlineNotFoundInsideDto() {
		Flight flight = makeValidFlight();
		AirlineDto dto = new AirlineDto();
		when(airlineClient.getAirline("AI")).thenReturn(dto);
		StepVerifier.create(service.addFlight(flight)).expectError(NotFoundException.class).verify();
	}

	@Test
	void testAddFlight_SourceEqualsDestination() {
		Flight flight = makeValidFlight();
		flight.setToCity(AIRPORT_CODE.DEL);
		AirlineDto dto = new AirlineDto();
		dto.setCode("AI");
		when(airlineClient.getAirline("AI")).thenReturn(dto);
		StepVerifier.create(service.addFlight(flight)).expectError(BusinessException.class).verify();
	}

	@Test
	void testAddFlight_ArrivalBeforeDeparture() {
		Flight flight = makeValidFlight();
		flight.setArrivalTime(flight.getDepartureTime().minusHours(2));
		AirlineDto dto = new AirlineDto();
		dto.setCode("AI");
		when(airlineClient.getAirline("AI")).thenReturn(dto);
		StepVerifier.create(service.addFlight(flight)).expectError(BusinessException.class).verify();
	}

	@Test
	void testAddFlight_DeparturePast() {
		Flight flight = makeValidFlight();
		flight.setDepartureTime(LocalDateTime.now().minusDays(1));
		AirlineDto dto = new AirlineDto();
		dto.setCode("AI");
		when(airlineClient.getAirline("AI")).thenReturn(dto);
		StepVerifier.create(service.addFlight(flight)).expectError(BusinessException.class).verify();
	}

	@Test
	void testAddFlight_FallbackMethod() {
		Flight flight = makeValidFlight();
		StepVerifier.create(service.addFlightFallback(flight, new RuntimeException()))
				.expectError(BusinessException.class).verify();
	}

	@Test
	void testSearchFlights() {
		Flight f = makeValidFlight();
		f.setDepartureTime(LocalDateTime.now().plusDays(2));
		when(flightRepo.findByFromCityAndToCity(AIRPORT_CODE.DEL, AIRPORT_CODE.BOM)).thenReturn(Flux.just(f));
		StepVerifier
				.create(service.searchFlights(AIRPORT_CODE.DEL, AIRPORT_CODE.BOM, f.getDepartureTime().toLocalDate()))
				.expectNext(f).verifyComplete();
	}

	@Test
	void testGetFlightById_Success() {
		Flight f = makeValidFlight();
		when(flightRepo.findById("F1")).thenReturn(Mono.just(f));
		StepVerifier.create(service.getFlightById("F1")).expectNext(f).verifyComplete();
	}

	@Test
	void testGetFlightById_NotFound() {
		when(flightRepo.findById("F1")).thenReturn(Mono.empty());
		StepVerifier.create(service.getFlightById("F1")).expectError(NotFoundException.class).verify();
	}

	@Test
	void testGetFlightsByAirline() {
		Flight f = makeValidFlight();
		when(flightRepo.findByAirlineCode("AI")).thenReturn(Flux.just(f));
		StepVerifier.create(service.getFlightsByAirline("AI")).expectNext(f).verifyComplete();
	}
}
