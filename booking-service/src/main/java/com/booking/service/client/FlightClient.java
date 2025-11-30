package com.booking.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.booking.service.dto.FlightDto;

@FeignClient(name = "flight-service")
public interface FlightClient {

    @GetMapping("/flights/get/{flightId}")
    FlightDto getFlight(@PathVariable("flightId") String flightId);
}
