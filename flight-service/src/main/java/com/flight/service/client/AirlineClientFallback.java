package com.flight.service.client;

import org.springframework.stereotype.Component;

import com.flight.service.dto.AirlineDto;
import com.flight.service.exception.AirlineServiceUnavailableException;

@Component
public class AirlineClientFallback implements AirlineClient {

    @Override
    public AirlineDto getAirline(String code) {
        // We throw here so CircuitBreaker can trigger fallback method in service
    	throw new AirlineServiceUnavailableException("Airline service is unavailable");

    }
}
