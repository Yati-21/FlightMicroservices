package com.user.service.service;

import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.user.service.entity.User;
import com.user.service.exception.BusinessException;
import com.user.service.exception.NotFoundException;
import com.user.service.repository.UserRepository;
import com.user.service.request.UserCreateRequest;
import com.user.service.request.UserUpdateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

	private static final String USER_NOT_FOUND = "User not found";
	private final UserRepository repo;
	private final WebClient.Builder webClientBuilder;
	private final ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory;

	public UserServiceImpl(UserRepository repo, WebClient.Builder webClientBuilder,
			ReactiveCircuitBreakerFactory<?, ?> circuitBreakerFactory) {
		this.repo = repo;
		this.webClientBuilder = webClientBuilder;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	@Override
	public Mono<User> createUser(UserCreateRequest req) {
		User user = new User(null, req.getName(), req.getEmail());
		return repo.save(user);
	}

	@Override
	public Mono<User> getUser(String id) {
		return repo.findById(id).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)));
	}

	@Override
	public Mono<User> getByEmail(String email) {
		return repo.findByEmail(email)
				.switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND + " with email: " + email)));
	}

	@Override
	public Mono<User> updateUser(String id, UserUpdateRequest req) {
		return repo.findById(id).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND))).flatMap(existing -> {
			existing.setName(req.getName());
			existing.setEmail(req.getEmail());
			return repo.save(existing);
		});
	}

	@Override
	public Flux<User> getAllUsers() {
		return repo.findAll();
	}

//	@Override
//	public Mono<Void> deleteUser(String id) 
//	{
//		return repo.findById(id).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND))).flatMap(repo::delete);
//	}
	@Override
	public Mono<Void> deleteUser(String userId) {

		return userHasBookings(userId).flatMap(hasBookings -> {
			if (hasBookings) {
				return Mono.error(new BusinessException("User cannot be deleted because bookings exist"));
			}

			//if no bookings → safe to delete
			return repo.findById(userId).switchIfEmpty(Mono.error(new NotFoundException("User not found")))
					.flatMap(repo::delete);
		});
	}

	private Mono<Boolean> userHasBookings(String userId) {
		return webClientBuilder.build().get().uri("http://booking-service/bookings/history/user/{id}", userId)
				.retrieve().bodyToFlux(Object.class)
				.hasElements(); //true if bookings exist
	}

}
