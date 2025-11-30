package com.booking.service.client;

import org.springframework.stereotype.Component;

import com.booking.service.dto.FlightDto;

@Component
class FlightClientFallback implements FlightClient {
    @Override
    public FlightDto getFlight(String id) {
        throw new RuntimeException("Flight service is down");
    }
}