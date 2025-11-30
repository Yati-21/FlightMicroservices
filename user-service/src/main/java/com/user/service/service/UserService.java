package com.user.service.service;

import com.user.service.entity.User;
import com.user.service.request.UserCreateRequest;
import com.user.service.request.UserUpdateRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> createUser(UserCreateRequest req);

    Mono<User> getUser(String id);

    Mono<User> getByEmail(String email);

    Mono<User> updateUser(String id, UserUpdateRequest req);

    Flux<User> getAllUsers();

    Mono<Void> deleteUser(String id);
}
