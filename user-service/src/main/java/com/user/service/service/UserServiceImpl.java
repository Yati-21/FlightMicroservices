package com.user.service.service;

import org.springframework.stereotype.Service;

import com.user.service.entity.User;
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

	public UserServiceImpl(UserRepository repo) {
		this.repo = repo;
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

	@Override
	public Mono<Void> deleteUser(String id) {
		return repo.findById(id).switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND))).flatMap(repo::delete);
	}
}
