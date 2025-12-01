package com.booking.service.client;

import com.booking.service.dto.UserDto;
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
public class UserClientReactive {
	private final WebClient.Builder webClientBuilder;
	private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

	private static final String USER_CB = "userServiceCB";

	public Mono<UserDto> getUserById(String userId) {
		ReactiveCircuitBreaker cb = circuitBreakerFactory.create(USER_CB);

		Mono<UserDto> call = webClientBuilder.build().get().uri("http://user-service/users/{id}", userId).retrieve()
				.onStatus(HttpStatusCode::is4xxClientError, r -> Mono.error(new NotFoundException("User not found")))
				.bodyToMono(UserDto.class);
		return cb.run(call, t -> Mono.error(new BusinessException("User service unavailable")));
	}

	public Mono<UserDto> getUserByEmail(String email) {
		ReactiveCircuitBreaker cb = circuitBreakerFactory.create(USER_CB);
		Mono<UserDto> call = webClientBuilder.build().get().uri("http://user-service/users/email/{email}", email)
				.retrieve()
				.onStatus(HttpStatusCode::is4xxClientError,
						r -> Mono.error(new NotFoundException("User not found with email: " + email)))
				.bodyToMono(UserDto.class);
		return cb.run(call, t -> Mono.error(new BusinessException("User service unavailable")));
	}
}
