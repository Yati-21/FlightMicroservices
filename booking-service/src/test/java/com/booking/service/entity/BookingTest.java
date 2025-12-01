package com.booking.service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

	@Test
	void testBookingGettersSetters() {

		Booking booking = new Booking();
		booking.setId("B1");
		booking.setPnr("PNR123");
		booking.setUserId("U1");
		booking.setFlightId("F1");
		booking.setSeatsBooked(2);
		booking.setMealType(MEAL_TYPE.VEG);
		booking.setFlightType(FLIGHT_TYPE.ONE_WAY);
		booking.setPassengerIds(List.of("P1", "P2"));

		assertEquals("B1", booking.getId());
		assertEquals("PNR123", booking.getPnr());
		assertEquals("U1", booking.getUserId());
		assertEquals("F1", booking.getFlightId());
		assertEquals(2, booking.getSeatsBooked());
		assertEquals(MEAL_TYPE.VEG, booking.getMealType());
		assertEquals(FLIGHT_TYPE.ONE_WAY, booking.getFlightType());
		assertEquals(2, booking.getPassengerIds().size());
	}

	@Test
	void testBookingAllArgsConstructor() {
		Booking b = new Booking("B1", "PNR123", "U1", "F1", 1, MEAL_TYPE.VEG, FLIGHT_TYPE.ROUND_TRIP, List.of("P1"));
		assertEquals("B1", b.getId());
	}

	@Test
	void testBookingNoArgsConstructor() {
		Booking b = new Booking();
		assertNotNull(b);
	}
}
