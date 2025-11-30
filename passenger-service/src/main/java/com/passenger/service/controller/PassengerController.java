package com.passenger.service.controller;

import com.passenger.service.entity.Passenger;
import com.passenger.service.exception.NotFoundException;
import com.passenger.service.repository.PassengerRepository;
import com.passenger.service.request.PassengerRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/passengers")
public class PassengerController {
	private final PassengerRepository passengerRepo;

	public PassengerController(PassengerRepository passengerRepo) {
		this.passengerRepo = passengerRepo;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<String> addPassenger(@Valid @RequestBody PassengerRequest req) {

		Passenger p = new Passenger(null, req.getName(), req.getGender(), req.getAge(), req.getSeatNumber(),
				req.getBookingId());

		return passengerRepo.save(p).map(Passenger::getId);
	}

	@GetMapping("/booking/{bookingId}")
	public Flux<Passenger> getPassengersByBooking(@PathVariable String bookingId) {
		return passengerRepo.findByBookingId(bookingId);
	}

	@DeleteMapping("/booking/{bookingId}")
	public Mono<Void> deleteByBooking(@PathVariable String bookingId) {
		return passengerRepo.deleteByBookingId(bookingId);
	}
}
