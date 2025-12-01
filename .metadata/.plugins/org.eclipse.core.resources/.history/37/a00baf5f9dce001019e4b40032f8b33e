//package com.booking.service.service;
//
//import com.booking.service.client.FlightClient;
//import com.booking.service.client.UserClient;
//import com.booking.service.dto.FlightDto;
//import com.booking.service.dto.UserDto;
//import com.booking.service.entity.*;
//import com.booking.service.exception.*;
//import com.booking.service.repository.BookingRepository;
//import com.booking.service.repository.PassengerRepository;
//import com.booking.service.request.BookingRequest;
//import com.booking.service.request.PassengerRequest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class BookingServiceImplTest {
//
//    @Mock
//    private BookingRepository bookingRepo;
//
//    @Mock
//    private PassengerRepository passengerRepo;
//
//    @Mock
//    private UserClient userClient;
//
//    @Mock
//    private FlightClient flightClient;
//
//    @InjectMocks
//    private BookingServiceImpl service;
//
//    private BookingRequest bookingRequest;
//    private PassengerRequest passengerReq;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        passengerReq = new PassengerRequest();
//        passengerReq.setName("John");
//        passengerReq.setGender(GENDER.M);
//        passengerReq.setAge(25);
//        passengerReq.setSeatNumber("A1");
//
//        bookingRequest = new BookingRequest();
//        bookingRequest.setFlightId("FL1");
//        bookingRequest.setUserId("U1");
//        bookingRequest.setSeatsBooked(1);
//        bookingRequest.setPassengers(List.of(passengerReq));
//        bookingRequest.setMealType(MEAL_TYPE.VEG);
//        bookingRequest.setFlightType(FLIGHT_TYPE.ONE_WAY);
//    }
//
//    // SUCCESS: Book ticket
//    @Test
//    void testBookTicketSuccess() {
//        UserDto user = new UserDto();
//        user.setId("U1");
//
//        FlightDto flight = new FlightDto();
//        flight.setId("FL1");
//        flight.setTotalSeats(100);
//
//        when(bookingRepo.save(any())).thenAnswer(inv -> {
//            Booking b = inv.getArgument(0);
//            b.setId("B1");
//            return Mono.just(b);
//        });
//
//        when(userClient.getUserById("U1")).thenReturn(user);
//        when(flightClient.getFlight("FL1")).thenReturn(flight);
//
//        when(bookingRepo.findByFlightId("FL1")).thenReturn(Flux.empty());
//
//        when(passengerRepo.save(any())).thenAnswer(inv -> {
//            Passenger p = inv.getArgument(0);
//            p.setId("P1");
//            return Mono.just(p);
//        });
//
//        StepVerifier.create(service.bookTicket(bookingRequest))
//                .assertNext(pnr -> {
//                    assert pnr.startsWith("PNR");
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void testUserNotFound() {
//        doThrow(new RuntimeException("404"))
//                .when(userClient).getUserById("U1");
//
//        StepVerifier.create(service.bookTicket(bookingRequest))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testFlightNotFound() {
//        UserDto user = new UserDto();
//        user.setId("U1");
//
//        when(userClient.getUserById("U1")).thenReturn(user);
//        doThrow(new RuntimeException("404"))
//                .when(flightClient).getFlight("FL1");
//
//        StepVerifier.create(service.bookTicket(bookingRequest))
//                .expectError(NotFoundException.class)
//                .verify();
//    }
//
//    @Test
//    void testSeatCountMismatch() {
//        bookingRequest.setSeatsBooked(2);
//
//        StepVerifier.create(service.bookTicket(bookingRequest))
//                .expectError(BusinessException.class)
//                .verify();
//    }
//
//    @Test
//    void testDuplicateSeats() {
//        PassengerRequest p2 = new PassengerRequest();
//        p2.setName("A");
//        p2.setGender(GENDER.F);
//        p2.setAge(20);
//        p2.setSeatNumber("A1");
//
//        bookingRequest.setSeatsBooked(2);
//        bookingRequest.setPassengers(List.of(passengerReq, p2));
//
//        StepVerifier.create(service.bookTicket(bookingRequest))
//                .expectError(BusinessException.class)
//                .verify();
//    }
//
//    @Test
//    void testCancelBookingSuccess() {
//        Booking booking = new Booking();
//        booking.setId("B1");
//        booking.setFlightId("FL1");
//
//        FlightDto flight = new FlightDto();
//        flight.setDepartureTime(LocalDateTime.now().plusHours(30));
//
//        when(bookingRepo.findByPnr("PNR1")).thenReturn(Mono.just(booking));
//        when(flightClient.getFlight("FL1")).thenReturn(flight);
//        when(passengerRepo.findByBookingId("B1")).thenReturn(Flux.empty());
//        when(bookingRepo.delete(booking)).thenReturn(Mono.empty());
//
//        StepVerifier.create(service.cancelBooking("PNR1"))
//                .verifyComplete();
//    }
//
//    @Test
//    void testCancelBookingTooLate() {
//        Booking booking = new Booking();
//        booking.setId("B1");
//        booking.setFlightId("FL1");
//
//        FlightDto flight = new FlightDto();
//        flight.setDepartureTime(LocalDateTime.now().plusHours(2));
//
//        when(bookingRepo.findByPnr("PNR1")).thenReturn(Mono.just(booking));
//        when(flightClient.getFlight("FL1")).thenReturn(flight);
//
//        StepVerifier.create(service.cancelBooking("PNR1"))
//                .expectError(BusinessException.class)
//                .verify();
//    }
//}
