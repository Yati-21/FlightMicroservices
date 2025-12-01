package com.passenger.service.service;

import com.passenger.service.entity.Passenger;
import com.passenger.service.exception.NotFoundException;
import com.passenger.service.repository.PassengerRepository;
import com.passenger.service.request.PassengerCreateRequest;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository repo;

    public PassengerServiceImpl(PassengerRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<String> createPassenger(PassengerCreateRequest req) {

        Passenger p = new Passenger();
        p.setName(req.getName());
        p.setGender(req.getGender());
        p.setAge(req.getAge());
        p.setSeatNumber(req.getSeatNumber());
        p.setBookingId(req.getBookingId());

        return repo.save(p).map(Passenger::getId);
    }

    @Override
    public Flux<Passenger> getByBookingId(String bookingId) {
        return repo.findByBookingId(bookingId);
    }

    @Override
    public Mono<Void> deleteByBookingId(String bookingId) {
        return repo.findByBookingId(bookingId)
                .flatMap(p -> repo.deleteById(p.getId()))
                .then();
    }
}
