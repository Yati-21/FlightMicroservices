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
    public Mono<String> createUser(@RequestBody @Valid UserCreateRequest req) {
        return service.createUser(req);
    }

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return service.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public Mono<User> getUserByEmail(@PathVariable String email) {
        return service.getUserByEmail(email);
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return service.getAllUsers();
    }

    @PutMapping("/{id}")
    public Mono<User> update(@PathVariable String id, @RequestBody @Valid UserUpdateRequest req) {
        return service.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.deleteUser(id);
    }
}
