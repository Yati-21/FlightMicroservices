package com.booking.service.service;

import com.booking.service.client.FlightClient;
import com.booking.service.client.PassengerClient;
import com.booking.service.client.UserClient;
import com.booking.service.dto.FlightDto;
import com.booking.service.dto.PassengerCreateRequest;
import com.booking.service.dto.PassengerDto;
import com.booking.service.dto.UserDto;
import com.booking.service.entity.*;
import com.booking.service.exception.*;
import com.booking.service.repository.BookingRepository;
import com.booking.service.request.BookingRequest;
import com.booking.service.request.PassengerRequest;

import org.junit.jupiter.api.*;
import org.mockito.*;
import reactor.core.publisher.*;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private UserClient userClient;

    @Mock
    private FlightClient flightClient;

    @Mock
    private PassengerClient passengerClient;

    @InjectMocks
    private BookingServiceImpl service;

    private BookingRequest bookingRequest;
    private PassengerRequest passengerReq;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        passengerReq = new PassengerRequest();
        passengerReq.setName("John");
        passengerReq.setGender(GENDER.M);
        passengerReq.setAge(25);
        passengerReq.setSeatNumber("A1");

        bookingRequest = new BookingRequest();
        bookingRequest.setFlightId("FL1");
        bookingRequest.setUserId("U1");
        bookingRequest.setSeatsBooked(1);
        bookingRequest.setPassengers(List.of(passengerReq));
        bookingRequest.setMealType(MEAL_TYPE.VEG);
        bookingRequest.setFlightType(FLIGHT_TYPE.ONE_WAY);
    }

    // ---------------------- bookTicket tests --------------------------

    @Test
    void testBookTicketSuccess() {
        UserDto user = new UserDto();
        user.setId("U1");

        FlightDto flight = new FlightDto();
        flight.setId("FL1");
        flight.setTotalSeats(100);

        Booking booking = new Booking();
        booking.setId("B1");

        when(userClient.getUserById("U1")).thenReturn(user);
        when(flightClient.getFlight("FL1")).thenReturn(flight);
        when(bookingRepo.findByFlightId("FL1")).thenReturn(Flux.empty());
        when(bookingRepo.save(any())).thenReturn(Mono.just(booking));

        when(passengerClient.createPassenger(any(PassengerCreateRequest.class))).thenReturn("P1");

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectNextMatches(pnr -> pnr.startsWith("PNR"))
                .verifyComplete();
    }

    @Test
    void testBookTicket_userNotFound() {
        when(userClient.getUserById("U1")).thenThrow(new RuntimeException("Not Found"));

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testBookTicket_flightNotFound() {
        UserDto user = new UserDto();
        user.setId("U1");

        when(userClient.getUserById("U1")).thenReturn(user);
        when(flightClient.getFlight("FL1")).thenThrow(new RuntimeException("Not found"));

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testBookTicket_seatCountMismatch() {
        bookingRequest.setSeatsBooked(2);

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void testBookTicket_duplicateSeats() {
        PassengerRequest p2 = new PassengerRequest();
        p2.setName("Alice");
        p2.setGender(GENDER.F);
        p2.setAge(22);
        p2.setSeatNumber("A1");

        bookingRequest.setSeatsBooked(2);
        bookingRequest.setPassengers(List.of(passengerReq, p2));

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(BusinessException.class)
                .verify();
    }

    @Test
    void testBookTicket_noAvailableSeats() {
        UserDto user = new UserDto();
        user.setId("U1");

        FlightDto flight = new FlightDto();
        flight.setId("FL1");
        flight.setTotalSeats(1);

        Booking prev = new Booking();
        prev.setSeatsBooked(1);

        when(userClient.getUserById("U1")).thenReturn(user);
        when(flightClient.getFlight("FL1")).thenReturn(flight);
        when(bookingRepo.findByFlightId("FL1")).thenReturn(Flux.just(prev));

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(SeatUnavailableException.class)
                .verify();
    }

    @Test
    void testBookTicket_seatConflict() {
        UserDto user = new UserDto();
        user.setId("U1");

        FlightDto flight = new FlightDto();
        flight.setId("FL1");
        flight.setTotalSeats(100);

        Booking booking = new Booking();
        booking.setId("B1");

        PassengerDto p1 = new PassengerDto();
        p1.setSeatNumber("A1");

        when(userClient.getUserById("U1")).thenReturn(user);
        when(flightClient.getFlight("FL1")).thenReturn(flight);
        when(bookingRepo.findByFlightId("FL1")).thenReturn(Flux.just(booking));
        when(passengerClient.getPassengersByBooking("B1")).thenReturn(List.of(p1));

        StepVerifier.create(service.bookTicket(bookingRequest))
                .expectError(SeatUnavailableException.class)
                .verify();
    }

    // ---------------- cancelBooking tests ----------------

    @Test
    void testCancelBookingSuccess() {
        Booking booking = new Booking();
        booking.setId("B1");
        booking.setFlightId("FL1");

        FlightDto flight = new FlightDto();
        flight.setDepartureTime(LocalDateTime.now().plusHours(48));

        when(bookingRepo.findByPnr("PNR1")).thenReturn(Mono.just(booking));
        when(flightClient.getFlight("FL1")).thenReturn(flight);
        doNothing().when(passengerClient).deleteByBooking("B1");
        when(bookingRepo.delete(booking)).thenReturn(Mono.empty());

        StepVerifier.create(service.cancelBooking("PNR1"))
                .verifyComplete();
    }

    @Test
    void testCancelBooking_Within24Hours() {
        Booking booking = new Booking();
        booking.setId("B1");
        booking.setFlightId("FL1");

        FlightDto flight = new FlightDto();
        flight.setDepartureTime(LocalDateTime.now().plusHours(2));

        when(bookingRepo.findByPnr("PNR1")).thenReturn(Mono.just(booking));
        when(flightClient.getFlight("FL1")).thenReturn(flight);

        StepVerifier.create(service.cancelBooking("PNR1"))
                .expectError(BusinessException.class)
                .verify();
    }
}
