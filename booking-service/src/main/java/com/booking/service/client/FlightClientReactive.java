package com.booking.service.client;

import com.booking.service.dto.FlightDto;
import com.booking.service.exception.BusinessException;
import com.booking.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlightClientReactive {

	private final WebClient.Builder webClientBuilder;
	private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

	private static final String FLIGHT_CB = "flightServiceCB";

	public Mono<FlightDto> getFlightById(String flightId) {

		ReactiveCircuitBreaker cb = circuitBreakerFactory.create(FLIGHT_CB);
		Mono<FlightDto> call = webClientBuilder.build().get().uri("http://flight-service/flights/get/{id}", flightId)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new NotFoundException("Flight not found")))
				.bodyToMono(FlightDto.class);
		return cb.run(call, t -> Mono.error(new BusinessException("Flight service unavailable")));
	}
}
