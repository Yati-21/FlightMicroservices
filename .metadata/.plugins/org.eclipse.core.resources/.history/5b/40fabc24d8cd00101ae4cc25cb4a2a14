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

    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<String> createUser(UserCreateRequest req) {
        User user = new User(null, req.getName(), req.getEmail());
        return repo.save(user).map(User::getId);
    }

    @Override
    public Mono<User> getUserById(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    @Override
    public Mono<User> getUserByEmail(String email) {
        return repo.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with email: " + email)));
    }

    @Override
    public Flux<User> getAllUsers() {
        return repo.findAll();
    }

    @Override
    public Mono<User> updateUser(String id, UserUpdateRequest req) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(existing -> {
                    existing.setName(req.getName());
                    existing.setEmail(req.getEmail());
                    return repo.save(existing);
                });
    }

    @Override
    public Mono<Void> deleteUser(String id) {
        return repo.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> repo.deleteById(user.getId()));
    }
}
