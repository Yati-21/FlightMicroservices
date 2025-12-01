package com.booking.service.service;

import com.booking.service.client.FlightClientReactive;
import com.booking.service.client.UserClientReactive;
import com.booking.service.dto.FlightDto;
import com.booking.service.dto.UserDto;
import com.booking.service.entity.*;
import com.booking.service.exception.BusinessException;
import com.booking.service.exception.NotFoundException;
import com.booking.service.exception.SeatUnavailableException;
import com.booking.service.repository.BookingRepository;
import com.booking.service.repository.PassengerRepository;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

	private BookingRepository bookingRepo;
	private PassengerRepository passengerRepo;
	private UserClientReactive userClient;
	private FlightClientReactive flightClient;
	private KafkaTemplate<String, Object> kafkaTemplate;
	private BookingServiceImpl service;

	@BeforeEach
	void setup() {
		bookingRepo = mock(BookingRepository.class);
		passengerRepo = mock(PassengerRepository.class);
		userClient = mock(UserClientReactive.class);
		flightClient = mock(FlightClientReactive.class);
		kafkaTemplate = mock(KafkaTemplate.class); 
		service = new BookingServiceImpl(bookingRepo, userClient, flightClient, passengerRepo, kafkaTemplate 
		);
	}

	private BookingRequest makeValidReq() {
		BookingRequest bookingReq = new BookingRequest();
		bookingReq.setUserId("U1");
		bookingReq.setFlightId("F1");
		bookingReq.setSeatsBooked(1);
		bookingReq.setFlightType(FLIGHT_TYPE.ONE_WAY);
		bookingReq.setMealType(MEAL_TYPE.VEG);

		PassengerRequest passengerReq = new PassengerRequest();
		passengerReq.setSeatNumber("A1");
		passengerReq.setGender(GENDER.M);
		passengerReq.setAge(25);
		passengerReq.setName("John");

		bookingReq.setPassengers(List.of(passengerReq));
		return bookingReq;
	}

	@Test
	void testBookTicket_Success() {
		BookingRequest bookingReq = makeValidReq();
		UserDto user = new UserDto();
		user.setId("U1");

		FlightDto flight = new FlightDto();
		flight.setId("F1");
		flight.setTotalSeats(50);
		flight.setDepartureTime(LocalDateTime.now().plusDays(1));

		when(userClient.getUserById("U1")).thenReturn(Mono.just(user));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(flight));
		when(bookingRepo.findByFlightId("F1")).thenReturn(Flux.empty());

		Booking saved = new Booking();
		saved.setId("B1");
		saved.setPnr("PNR12345");
		when(bookingRepo.save(any())).thenReturn(Mono.just(saved));

		Passenger savedP = new Passenger();
		savedP.setId("P1");
		when(passengerRepo.save(any())).thenReturn(Mono.just(savedP));

		StepVerifier.create(service.bookTicket(bookingReq)).expectNextMatches(pnr -> pnr.startsWith("PNR"))
				.verifyComplete();
	}

	@Test
	void testBookTicket_PassengerCountMismatch() {
		BookingRequest bookingReq = makeValidReq();
		bookingReq.setSeatsBooked(2);
		StepVerifier.create(service.bookTicket(bookingReq)).expectError(BusinessException.class).verify();
	}

	@Test
	void testBookTicket_DuplicateSeat() {
		BookingRequest bookingReq = new BookingRequest();
		bookingReq.setUserId("U1");
		bookingReq.setFlightId("F1");
		bookingReq.setSeatsBooked(2);
		bookingReq.setFlightType(FLIGHT_TYPE.ONE_WAY);
		bookingReq.setMealType(MEAL_TYPE.VEG);

		PassengerRequest passengerReq1 = new PassengerRequest();
		passengerReq1.setSeatNumber("A1");
		passengerReq1.setGender(GENDER.M);
		passengerReq1.setAge(22);
		passengerReq1.setName("Bob");

		PassengerRequest passengerReq2 = new PassengerRequest();
		passengerReq2.setSeatNumber("A1");
		passengerReq2.setGender(GENDER.F);
		passengerReq2.setAge(30);
		passengerReq2.setName("Ana");

		bookingReq.setPassengers(List.of(passengerReq1, passengerReq2));
		StepVerifier.create(service.bookTicket(bookingReq)).expectError(BusinessException.class).verify();
	}

	@Test
	void testBookTicket_UserNotFound() {
		BookingRequest req = makeValidReq();
		when(userClient.getUserById("U1")).thenReturn(Mono.error(new NotFoundException("User not found")));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(new FlightDto()));
		StepVerifier.create(service.bookTicket(req)).expectError(NotFoundException.class).verify();
	}

	@Test
	void testBookTicket_FlightNotFound() {
		BookingRequest req = makeValidReq();
		when(userClient.getUserById("U1")).thenReturn(Mono.just(new UserDto()));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.error(new NotFoundException("Flight not found")));
		StepVerifier.create(service.bookTicket(req)).expectError(NotFoundException.class).verify();
	}

	@Test
	void testBookTicket_NotEnoughSeats() {
		BookingRequest req = makeValidReq();
		UserDto user = new UserDto();
		user.setId("U1");

		FlightDto flight = new FlightDto();
		flight.setId("F1");
		flight.setTotalSeats(1);
		Booking existing = new Booking();
		existing.setSeatsBooked(1);
		when(userClient.getUserById("U1")).thenReturn(Mono.just(user));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(flight));
		when(bookingRepo.findByFlightId("F1")).thenReturn(Flux.just(existing));

		StepVerifier.create(service.bookTicket(req)).expectError(SeatUnavailableException.class).verify();
	}

	@Test
	void testGetTicket_Success() {
		Booking booking = new Booking();
		booking.setPnr("PNR123");

		when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.just(booking));
		StepVerifier.create(service.getTicket("PNR123")).expectNext(booking).verifyComplete();
	}

	@Test
	void testGetTicket_NotFound() {
		when(bookingRepo.findByPnr("PNR123")).thenReturn(Mono.empty());
		StepVerifier.create(service.getTicket("PNR123")).expectError(NotFoundException.class).verify();
	}

	@Test
	void testHistoryByUserId_Success() {
		Booking booking = new Booking();
		booking.setUserId("U1");

		when(bookingRepo.findByUserId("U1")).thenReturn(Flux.just(booking));
		StepVerifier.create(service.getBookingHistoryByUserId("U1")).expectNext(booking).verifyComplete();
	}

	@Test
	void testHistoryByEmail_UserIdNull() {
		UserDto user = new UserDto();
		when(userClient.getUserByEmail("e@mail.com")).thenReturn(Mono.just(user));
		StepVerifier.create(service.getBookingHistoryByEmail("e@mail.com")).expectError(NotFoundException.class)
				.verify();
	}

	@Test
	void testHistoryByEmail_Success() {
		UserDto user = new UserDto();
		user.setId("U1");
		Booking booking = new Booking();
		booking.setUserId("U1");

		when(userClient.getUserByEmail("e@mail.com")).thenReturn(Mono.just(user));
		when(bookingRepo.findByUserId("U1")).thenReturn(Flux.just(booking));
		StepVerifier.create(service.getBookingHistoryByEmail("e@mail.com")).expectNext(booking).verifyComplete();
	}

	@Test
	void testCancelBooking_Success() {
		Booking booking = new Booking();
		booking.setId("B1");
		booking.setFlightId("F1");
		FlightDto flight = new FlightDto();
		flight.setDepartureTime(LocalDateTime.now().plusDays(3));

		when(bookingRepo.findByPnr("PNR")).thenReturn(Mono.just(booking));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(flight));
		when(passengerRepo.findByBookingId("B1")).thenReturn(Flux.empty());
		when(bookingRepo.delete(booking)).thenReturn(Mono.empty());
		StepVerifier.create(service.cancelBooking("PNR")).verifyComplete();
	}

	@Test
	void testCancelBooking_TooLate() {
		Booking booking = new Booking();
		booking.setId("B1");
		booking.setFlightId("F1");
		FlightDto flight = new FlightDto();
		flight.setDepartureTime(LocalDateTime.now().plusHours(5));

		when(bookingRepo.findByPnr("PNR")).thenReturn(Mono.just(booking));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(flight));
		StepVerifier.create(service.cancelBooking("PNR")).expectError(BusinessException.class).verify();
	}

	@Test
	void testCancelBooking_InvalidPNR() {
		when(bookingRepo.findByPnr("BAD")).thenReturn(Mono.empty());
		StepVerifier.create(service.cancelBooking("BAD")).expectError(NotFoundException.class).verify();
	}

	@Test
	void testCheckSeatConflict_NoConflict() {

		BookingRequest req = makeValidReq();
		UserDto user = new UserDto();
		user.setId("U1");

		FlightDto flight = new FlightDto();
		flight.setId("F1");
		flight.setTotalSeats(100);

		when(userClient.getUserById("U1")).thenReturn(Mono.just(user));
		when(flightClient.getFlightById("F1")).thenReturn(Mono.just(flight));

		Booking existing = new Booking();
		existing.setId("B1");
		existing.setSeatsBooked(1);
		Passenger passenger = new Passenger();
		passenger.setSeatNumber("B2");

		when(bookingRepo.findByFlightId("F1")).thenReturn(Flux.just(existing));
		when(passengerRepo.findByBookingId("B1")).thenReturn(Flux.just(passenger));

		Booking saved = new Booking();
		saved.setId("BID");
		saved.setPnr("PNR123");
		when(bookingRepo.save(any())).thenReturn(Mono.just(saved));
		Passenger savedPassenger = new Passenger();
		savedPassenger.setId("P1");
		when(passengerRepo.save(any())).thenReturn(Mono.just(savedPassenger));

		StepVerifier.create(service.bookTicket(req)).expectNextMatches(pnr -> pnr.startsWith("PNR")).verifyComplete();
	}

}
