package com.booking.service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumTests {

	@Test
	void testAirportCodeValues() {
		for (AIRPORT_CODE code : AIRPORT_CODE.values()) {
			assertNotNull(code);
			assertNotNull(code.name());
		}
	}

	@Test
	void testFlightStatusValues() {
		for (FLIGHT_STATUS status : FLIGHT_STATUS.values()) {
			assertNotNull(status);
		}
	}

	@Test
	void testGenderValues() {
		for (GENDER g : GENDER.values()) {
			assertNotNull(g);
		}
	}

	@Test
	void testMealTypeValues() {
		for (MEAL_TYPE m : MEAL_TYPE.values()) {
			assertNotNull(m);
		}
	}

	@Test
	void testFlightTypeValues() {
		for (FLIGHT_TYPE ft : FLIGHT_TYPE.values()) {
			assertNotNull(ft);
		}
	}
}
