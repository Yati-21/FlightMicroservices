package com.airline.service.service;

import org.springframework.stereotype.Service;
import com.airline.service.entity.Airline;
import com.airline.service.exception.NotFoundException;
import com.airline.service.repository.AirlineRepository;
import com.airline.service.request.AirlineCreateRequest;
import com.airline.service.request.AirlineUpdateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AirlineServiceImpl implements AirlineService {

	private static final String ERR_AIRLINE_NOT_FOUND = "Airline not found";
	private final AirlineRepository repo;

	public AirlineServiceImpl(AirlineRepository repo) {
		this.repo = repo;
	}

	@Override
	public Mono<Airline> createAirline(AirlineCreateRequest req) {
		Airline airline = new Airline(req.getCode(), req.getName());
		return repo.save(airline);
	}

	@Override
	public Mono<Airline> getAirline(String code) {
		return repo.findById(code).switchIfEmpty(Mono.error(new NotFoundException(ERR_AIRLINE_NOT_FOUND)));
	}

	@Override
	public Flux<Airline> getAllAirlines() {
		return repo.findAll();
	}

	@Override
	public Mono<Airline> updateAirline(String code, AirlineUpdateRequest req) {
		return repo.findById(code).switchIfEmpty(Mono.error(new NotFoundException(ERR_AIRLINE_NOT_FOUND)))
				.flatMap(existing -> {
					existing.setName(req.getName());
					return repo.save(existing);
				});
	}

	@Override
	public Mono<Void> deleteAirline(String code) {
		return repo.findById(code).switchIfEmpty(Mono.error(new NotFoundException(ERR_AIRLINE_NOT_FOUND)))
				.flatMap(repo::delete);
	}
}
