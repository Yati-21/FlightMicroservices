package com.booking.service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.booking.service.dto.PassengerCreateRequest;
import com.booking.service.dto.PassengerDto;

@FeignClient(name = "passenger-service",fallback = PassengerClientFallback.class)
public interface PassengerClient {

    @PostMapping("/passengers")
    String createPassenger(@RequestBody PassengerCreateRequest req);

    @GetMapping("/passengers/booking/{bookingId}")
    List<PassengerDto> getPassengersByBooking(@PathVariable String bookingId);

    @DeleteMapping("/passengers/booking/{bookingId}")
    void deleteByBooking(@PathVariable String bookingId);
}
