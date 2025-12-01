package com.booking.service.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTests {

	@Test
	void testBusinessException() {
		BusinessException ex = new BusinessException("Business error");
		assertEquals("Business error", ex.getMessage());
	}

	@Test
	void testNotFoundException() {
		NotFoundException ex = new NotFoundException("Not found");
		assertEquals("Not found", ex.getMessage());
	}

	@Test
	void testSeatUnavailableException() {
		SeatUnavailableException ex = new SeatUnavailableException("No seats");
		assertEquals("No seats", ex.getMessage());
	}
}
