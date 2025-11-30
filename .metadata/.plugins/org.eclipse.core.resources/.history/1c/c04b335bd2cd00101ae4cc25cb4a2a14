package com.booking.service.client;

import java.util.List;

import org.springframework.stereotype.Component;

import com.booking.service.dto.PassengerCreateRequest;
import com.booking.service.dto.PassengerDto;

@Component
class PassengerClientFallback implements PassengerClient {
    @Override
    public List<PassengerDto> getPassengersByBooking(String bookingId) {
        throw new RuntimeException("Passenger service is down");
    }

    @Override
    public String createPassenger(PassengerCreateRequest req) {
        throw new RuntimeException("Passenger service is down");
    }

    @Override
    public void deleteByBooking(String bookingId) {
        throw new RuntimeException("Passenger service is down");
    }
}