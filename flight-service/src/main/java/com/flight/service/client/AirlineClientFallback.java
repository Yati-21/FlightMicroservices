package com.flight.service.client;

import org.springframework.stereotype.Component;

import com.flight.service.dto.AirlineDto;

@Component
public class AirlineClientFallback implements AirlineClient {

    @Override
    public AirlineDto getAirline(String code) {
        // We throw here so CircuitBreaker can trigger fallback method in service
        throw new RuntimeException("Airline service is down");
    }
}
