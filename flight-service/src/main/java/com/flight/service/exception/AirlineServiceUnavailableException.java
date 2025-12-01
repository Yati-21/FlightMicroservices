package com.flight.service.exception;

public class AirlineServiceUnavailableException extends RuntimeException {
    public AirlineServiceUnavailableException(String message) {
        super(message);
    }
}
