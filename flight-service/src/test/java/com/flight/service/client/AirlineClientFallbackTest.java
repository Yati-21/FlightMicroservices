package com.flight.service.client;

import com.flight.service.dto.AirlineDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirlineClientFallbackTest {

	private final AirlineClientFallback fallback = new AirlineClientFallback();

	@Test
	void testGetAirline_ThrowsFallbackException() {
		RuntimeException ex = assertThrows(RuntimeException.class, () -> fallback.getAirline("AI"));
		assertEquals("Airline service is down", ex.getMessage());
	}

	@Test
	void testFallbackIsComponent() {
		assertNotNull(fallback);
	}
}
