package com.user.service.service;

import com.user.service.entity.User;
import com.user.service.request.UserCreateRequest;
import com.user.service.request.UserUpdateRequest;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface UserService {

    Mono<String> createUser(UserCreateRequest req);

    Mono<User> getUserById(String id);

    Mono<User> getUserByEmail(String email);

    Flux<User> getAllUsers();

    Mono<User> updateUser(String id, UserUpdateRequest req);

    Mono<Void> deleteUser(String id);
}
