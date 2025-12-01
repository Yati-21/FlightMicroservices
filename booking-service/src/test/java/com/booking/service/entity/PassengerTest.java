package com.booking.service.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerTest {

	@Test
	void testPassengerGettersSetters() {
		Passenger ppassenger = new Passenger();
		ppassenger.setId("P1");
		ppassenger.setName("abc");
		ppassenger.setGender(GENDER.M);
		ppassenger.setAge(25);
		ppassenger.setSeatNumber("A10");
		ppassenger.setBookingId("B1");

		assertEquals("P1", ppassenger.getId());
		assertEquals("abc", ppassenger.getName());
		assertEquals(GENDER.M, ppassenger.getGender());
		assertEquals(25, ppassenger.getAge());
		assertEquals("A10", ppassenger.getSeatNumber());
		assertEquals("B1", ppassenger.getBookingId());
	}
}
