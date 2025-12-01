package com.booking.service.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    void testBookingGettersSetters() {

        Booking b = new Booking();
        b.setId("B1");
        b.setPnr("PNR123");
        b.setUserId("U1");
        b.setFlightId("F1");
        b.setSeatsBooked(2);
        b.setMealType(MEAL_TYPE.VEG);
        b.setFlightType(FLIGHT_TYPE.ONE_WAY);
        b.setPassengerIds(List.of("P1", "P2"));

        assertEquals("B1", b.getId());
        assertEquals("PNR123", b.getPnr());
        assertEquals("U1", b.getUserId());
        assertEquals("F1", b.getFlightId());
        assertEquals(2, b.getSeatsBooked());
        assertEquals(MEAL_TYPE.VEG, b.getMealType());
        assertEquals(FLIGHT_TYPE.ONE_WAY, b.getFlightType());
        assertEquals(2, b.getPassengerIds().size());
    }

    @Test
    void testBookingAllArgsConstructor() {
        Booking b = new Booking("B1", "PNR123", "U1", "F1",
                1, MEAL_TYPE.VEG, FLIGHT_TYPE.ROUND_TRIP, List.of("P1"));

        assertEquals("B1", b.getId());
    }

    @Test
    void testBookingNoArgsConstructor() {
        Booking b = new Booking();
        assertNotNull(b);
    }
}
