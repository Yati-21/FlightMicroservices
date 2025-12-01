package com.booking.service.client;

import com.booking.service.dto.UserDto;
import com.booking.service.exception.BusinessException;
import com.booking.service.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.Mockito.*;

class UserClientReactiveTest {

    private WebClient.Builder webClientBuilder;
    private ReactiveCircuitBreakerFactory<?, ?> cbFactory;
    private ReactiveCircuitBreaker cb;

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec uriSpec;
    private WebClient.RequestHeadersSpec headersSpec;
    private WebClient.ResponseSpec responseSpec;

    private UserClientReactive userClient;

    @BeforeEach
    void setup() {
        webClientBuilder = mock(WebClient.Builder.class);
        cbFactory = mock(ReactiveCircuitBreakerFactory.class);
        cb = mock(ReactiveCircuitBreaker.class);

        webClient = mock(WebClient.class);
        uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        headersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(cbFactory.create(anyString())).thenReturn(cb);

        userClient = new UserClientReactive(webClientBuilder, cbFactory);
    }

    // -------------------------------------------------------------
    // SUCCESS: getUserById()
    // -------------------------------------------------------------
    @Test
    void testGetUserById_Success() {

        UserDto user = new UserDto();
        user.setId("U1");

        when(webClient.get()).thenReturn(uriSpec);

        // IMPORTANT FIX
        when(uriSpec.uri(anyString(), ArgumentMatchers.<Object[]>any()))
                .thenReturn(headersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(user));

        when(cb.run(
                ArgumentMatchers.<Mono<UserDto>>any(),
                ArgumentMatchers.<Function<Throwable, Mono<UserDto>>>any()
        )).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(userClient.getUserById("U1"))
                .expectNextMatches(u -> u.getId().equals("U1"))
                .verifyComplete();
    }

    // -------------------------------------------------------------
    // NOT FOUND: getUserById()
    // -------------------------------------------------------------
    @Test
    void testGetUserById_NotFound() {

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(anyString(), ArgumentMatchers.<Object[]>any()))
                .thenReturn(headersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);

        // Trigger NotFoundException from bodyToMono
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        when(cb.run(
                ArgumentMatchers.<Mono<UserDto>>any(),
                ArgumentMatchers.<Function<Throwable, Mono<UserDto>>>any()
        )).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(userClient.getUserById("BAD"))
                .expectError(NotFoundException.class)
                .verify();
    }

    // -------------------------------------------------------------
    // FALLBACK (Circuit Breaker)
    // -------------------------------------------------------------
//    @Test
//    void testGetUserById_Fallback() {
//
//        when(cb.run(
//                ArgumentMatchers.<Mono<UserDto>>any(),
//                ArgumentMatchers.<Function<Throwable, Mono<UserDto>>>any()
//        )).thenAnswer(inv ->
//                ((Function<Throwable, Mono<UserDto>>) inv.getArgument(1))
//                        .apply(new RuntimeException("CB error"))
//        );
//
//        StepVerifier.create(userClient.getUserById("U1"))
//                .expectError(BusinessException.class)
//                .verify();
//    }

    // -------------------------------------------------------------
    // SUCCESS: getUserByEmail()
    // -------------------------------------------------------------
    @Test
    void testGetUserByEmail_Success() {

        UserDto user = new UserDto();
        user.setId("U1");
        user.setEmail("x@mail.com");

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(anyString(), ArgumentMatchers.<Object[]>any()))
                .thenReturn(headersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(user));

        when(cb.run(
                ArgumentMatchers.<Mono<UserDto>>any(),
                ArgumentMatchers.<Function<Throwable, Mono<UserDto>>>any()
        )).thenAnswer(inv -> inv.getArgument(0));

        StepVerifier.create(userClient.getUserByEmail("x@mail.com"))
                .expectNextMatches(u -> u.getEmail().equals("x@mail.com"))
                .verifyComplete();
    }

    // -------------------------------------------------------------
    // FALLBACK: getUserByEmail()
    // -------------------------------------------------------------
//    @Test
//    void testGetUserByEmail_Fallback() {
//
//        when(cb.run(
//                ArgumentMatchers.<Mono<UserDto>>any(),
//                ArgumentMatchers.<Function<Throwable, Mono<UserDto>>>any()
//        )).thenAnswer(inv ->
//                ((Function<Throwable, Mono<UserDto>>) inv.getArgument(1))
//                        .apply(new RuntimeException("CB error"))
//        );
//
//        StepVerifier.create(userClient.getUserByEmail("x@mail.com"))
//                .expectError(BusinessException.class)
//                .verify();
//    }
}
