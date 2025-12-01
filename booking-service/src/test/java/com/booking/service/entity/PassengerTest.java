package com.booking.service.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerTest {

    @Test
    void testPassengerGettersSetters() {
        Passenger p = new Passenger();
        p.setId("P1");
        p.setName("John");
        p.setGender(GENDER.M);
        p.setAge(25);
        p.setSeatNumber("A10");
        p.setBookingId("B1");

        assertEquals("P1", p.getId());
        assertEquals("John", p.getName());
        assertEquals(GENDER.M, p.getGender());
        assertEquals(25, p.getAge());
        assertEquals("A10", p.getSeatNumber());
        assertEquals("B1", p.getBookingId());
    }
}
