package com.flight.service.request;

import java.time.LocalDate;

import com.flight.service.entity.AIRPORT_CODE;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightSearchRequest {

    @NotNull(message = "fromCity is required")
    private AIRPORT_CODE fromCity;

    @NotNull(message = "toCity is required")
    private AIRPORT_CODE toCity;

    @NotNull(message = "date is required")
    private LocalDate date;
}
