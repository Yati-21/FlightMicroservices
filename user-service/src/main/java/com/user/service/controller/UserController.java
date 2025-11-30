package com.user.service.controller;

import com.user.service.entity.User;
import com.user.service.request.UserCreateRequest;
import com.user.service.request.UserUpdateRequest;
import com.user.service.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<User> createUser(@RequestBody @Valid UserCreateRequest req) {
		return service.createUser(req);
	}

	@GetMapping("/{id}")
	public Mono<User> getUser(@PathVariable String id) {
		return service.getUser(id);
	}

	@GetMapping("/email/{email}")
	public Mono<User> getByEmail(@PathVariable String email) {
		return service.getByEmail(email);
	}

	@PutMapping("/{id}")
	public Mono<User> updateUser(@PathVariable String id, @RequestBody @Valid UserUpdateRequest req) {
		return service.updateUser(id, req);
	}

	@GetMapping
	public Flux<User> getAll() {
		return service.getAllUsers();
	}

	@DeleteMapping("/{id}")
	public Mono<Void> deleteUser(@PathVariable String id) {
		return service.deleteUser(id);
	}
}
