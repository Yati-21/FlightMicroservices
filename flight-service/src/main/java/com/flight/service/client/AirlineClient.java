package com.flight.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.flight.service.dto.AirlineDto;

@FeignClient(name = "airline-service", fallback = AirlineClientFallback.class)
public interface AirlineClient {

    @GetMapping("/airlines/{code}")
    AirlineDto getAirline(@PathVariable("code") String code);
}
