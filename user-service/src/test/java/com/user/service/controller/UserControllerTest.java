package com.user.service.controller;

import com.user.service.entity.User;
import com.user.service.request.UserCreateRequest;
import com.user.service.request.UserUpdateRequest;
import com.user.service.service.UserService;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private UserService service;

    @Test
    void testCreateUser() {
        UserCreateRequest req = new UserCreateRequest();
        req.setName("jay");
        req.setEmail("jay@mail.com");
        User saved = new User("1", "jay", "jay@mail.com");
        Mockito.when(service.createUser(Mockito.any()))
                .thenReturn(Mono.just(saved));
        client.post().uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.name").isEqualTo("jay");
    }

    @Test
    void testGetUser() {
        User user = new User("1", "jay", "jay@mail.com");
        Mockito.when(service.getUser("1")).thenReturn(Mono.just(user));
        client.get().uri("/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("jay");
    }

    @Test
    void testGetByEmail() {
        User user = new User("1", "jay", "jay@mail.com");
        Mockito.when(service.getByEmail("jay@mail.com"))
                .thenReturn(Mono.just(user));

        client.get().uri("/users/email/jay@mail.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1");
    }

    @Test
    void testUpdateUser() {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setName("New");
        req.setEmail("new@mail.com");
        User updated = new User("1", "New", "new@mail.com");
        Mockito.when(service.updateUser(Mockito.eq("1"), Mockito.any()))
                .thenReturn(Mono.just(updated));

        client.put().uri("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New");
    }

    @Test
    void testDeleteUser() {
        Mockito.when(service.deleteUser("1"))
                .thenReturn(Mono.empty());
        client.delete().uri("/users/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User("1", "jay", "jay@mail.com");
        User user2 = new User("2", "riya", "riya@mail.com");

        Mockito.when(service.getAllUsers())
                .thenReturn(Flux.just(user1, user2));
        client.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("jay")
                .jsonPath("$[1].name").isEqualTo("riya");
    }
}
