package com.booking.service.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.booking.service.client.FlightClientReactive;
import com.booking.service.client.UserClientReactive;
import com.booking.service.dto.FlightDto;
import com.booking.service.dto.UserDto;
import com.booking.service.entity.Booking;
import com.booking.service.entity.Passenger;
import com.booking.service.event.BookingCreatedEvent;
import com.booking.service.exception.BusinessException;
import com.booking.service.exception.NotFoundException;
import com.booking.service.exception.SeatUnavailableException;
import com.booking.service.repository.BookingRepository;
import com.booking.service.repository.PassengerRepository;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

	private static final int CANCELLATION_LIMIT_HOURS = 24;

	private final BookingRepository bookingRepo;
	private final UserClientReactive userClient;
	private final FlightClientReactive flightClient;
	private final PassengerRepository passengerRepo;
	private final KafkaTemplate<String, Object> kafkaTemplate;

	public BookingServiceImpl(BookingRepository bookingRepo, UserClientReactive userClient,
			FlightClientReactive flightClient, PassengerRepository passengerRepo,
			KafkaTemplate<String, Object> kafkaTemplate) {
		this.bookingRepo = bookingRepo;
		this.userClient = userClient;
		this.flightClient = flightClient;
		this.passengerRepo = passengerRepo;
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public Mono<String> bookTicket(BookingRequest request) {

		if (request.getPassengers().size() != request.getSeatsBooked()) {
			return Mono.error(new BusinessException("Passengers count must be equal to seats booked"));
		}
		try {
			validatePassengerDuplicateRequest(request.getPassengers());
		} catch (BusinessException ex) {
			return Mono.error(ex);
		}

		Mono<UserDto> userMono = userClient.getUserById(request.getUserId());

		Mono<FlightDto> flightMono = flightClient.getFlightById(request.getFlightId());

		return Mono.zip(userMono, flightMono).flatMap(tuple -> {
			UserDto user = tuple.getT1();
			FlightDto flight = tuple.getT2();

			return bookingRepo.findByFlightId(request.getFlightId()).collectList().flatMap(existingBookings -> {

				int alreadyBooked = existingBookings.stream().mapToInt(Booking::getSeatsBooked).sum();

				int available = flight.getTotalSeats() - alreadyBooked;
				if (available < request.getSeatsBooked()) {
					return Mono.error(new SeatUnavailableException("Not enough seats available"));
				}

				return checkSeatConflictsReactive(existingBookings, request.getPassengers())
						.then(saveNewBookingReactive(flight, user, request));
			});
		});
	}

	@Override
	public Mono<Booking> getTicket(String pnr) {
		return bookingRepo.findByPnr(pnr).switchIfEmpty(Mono.error(new NotFoundException("PNR not found")));
	}

	@Override
	public Flux<Booking> getBookingHistoryByUserId(String userId) {
		return bookingRepo.findByUserId(userId);
	}

	@Override
	public Flux<Booking> getBookingHistoryByEmail(String email) {

		// 1. get user by email through user service
		return userClient.getUserByEmail(email).flux().flatMap(user -> {
			if (user == null || user.getId() == null) {
				return Flux.error(new NotFoundException("User not found with email: " + email));
			}
			return bookingRepo.findByUserId(user.getId());
		});
	}

	@Override
	public Mono<Void> cancelBooking(String pnr) {

		return bookingRepo.findByPnr(pnr).switchIfEmpty(Mono.error(new NotFoundException("Invalid PNR")))
				.flatMap(booking ->
				// get flight details via reactive client
				flightClient.getFlightById(booking.getFlightId()).flatMap(flight -> {

					long hoursDiff = Duration.between(LocalDateTime.now(), flight.getDepartureTime()).toHours();

					if (hoursDiff < CANCELLATION_LIMIT_HOURS) {
						return Mono.error(new BusinessException("Cannot cancel within 24 hours of departure"));
					}

					return passengerRepo.findByBookingId(booking.getId())
							.flatMap(p -> passengerRepo.deleteById(p.getId())).then(bookingRepo.delete(booking));
				}));
	}

	private void validatePassengerDuplicateRequest(List<PassengerRequest> passengers) {
		Set<String> seats = new HashSet<>();
		for (PassengerRequest passengerReq : passengers) {
			if (!seats.add(passengerReq.getSeatNumber())) {
				throw new BusinessException("Duplicate seat in request: " + passengerReq.getSeatNumber());
			}
		}
	}

	private Mono<Void> checkSeatConflictsReactive(List<Booking> existingBookings,
			List<PassengerRequest> newPassengers) {

		if (existingBookings.isEmpty()) {
			return Mono.empty();
		}

		return Flux.fromIterable(existingBookings).flatMap(b -> passengerRepo.findByBookingId(b.getId()))
				.map(Passenger::getSeatNumber).collect(Collectors.toSet()).flatMap(existingSeats -> {
					for (PassengerRequest req : newPassengers) {
						if (existingSeats.contains(req.getSeatNumber())) {
							return Mono
									.error(new SeatUnavailableException("Seat already booked: " + req.getSeatNumber()));
						}
					}
					return Mono.empty();
				});
	}

	private String generateRandomPNR() {
		return "PNR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private Mono<String> saveNewBookingReactive(FlightDto flight, UserDto user, BookingRequest req) {

		String pnr = generateRandomPNR();

		Booking booking = new Booking();
		booking.setPnr(pnr);
		booking.setFlightId(flight.getId());
		booking.setUserId(user.getId());
		booking.setSeatsBooked(req.getSeatsBooked());
		booking.setMealType(req.getMealType());
		booking.setFlightType(req.getFlightType());
		booking.setPassengerIds(new ArrayList<>());

		return bookingRepo.save(booking)
				.flatMap(savedBooking -> savePassengersReactive(savedBooking.getId(), req.getPassengers())
						.flatMap(passengerIds -> {
							savedBooking.setPassengerIds(passengerIds);

							return bookingRepo.save(savedBooking).doOnSuccess(b -> sendBookingCreatedEvent(b, user))
									.thenReturn(savedBooking.getPnr());
						}));

	}

	private void sendBookingCreatedEvent(Booking booking, UserDto user) {

		BookingCreatedEvent event = new BookingCreatedEvent(booking.getPnr(), user.getEmail(), user.getName(),
				booking.getFlightId(), booking.getSeatsBooked(), "Booking successful!");

		kafkaTemplate.send("booking-created", event);

		log.info("Sent Kafka event for booking PNR: {}", booking.getPnr());
	}

	private Mono<List<String>> savePassengersReactive(String bookingId, List<PassengerRequest> passengers) {

		return Flux.fromIterable(passengers).flatMap(req -> {
			Passenger passenger = new Passenger();
			passenger.setName(req.getName());
			passenger.setGender(req.getGender());
			passenger.setAge(req.getAge());
			passenger.setSeatNumber(req.getSeatNumber());
			passenger.setBookingId(bookingId);
			return passengerRepo.save(passenger);
		}).map(Passenger::getId).collectList();
	}
}
