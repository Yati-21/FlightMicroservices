//package com.user.service.service;
//
//import com.user.service.entity.User;
//import com.user.service.exception.NotFoundException;
//import com.user.service.repository.UserRepository;
//import com.user.service.request.UserCreateRequest;
//import com.user.service.request.UserUpdateRequest;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//public class UserServiceImplTest {
//
//	private UserRepository repo;
//	private UserServiceImpl service;
//
//	@BeforeEach
//	void setup() {
//		repo = Mockito.mock(UserRepository.class);
//		service = new UserServiceImpl(repo);
//	}
//
//	@Test
//	void testCreateUser_success() {
//		UserCreateRequest req = new UserCreateRequest();
//		req.setName("riya");
//		req.setEmail("riya@mail.com");
//		User saved = new User("1", "riya", "riya@mail.com");
//		Mockito.when(repo.save(Mockito.any(User.class))).thenReturn(Mono.just(saved));
//
//		StepVerifier.create(service.createUser(req)).expectNext(saved).verifyComplete();
//	}
//
//	@Test
//	void testGetUser_success() {
//		User user = new User("1", "jay", "jay@mail.com");
//		Mockito.when(repo.findById("1")).thenReturn(Mono.just(user));
//		StepVerifier.create(service.getUser("1")).expectNext(user).verifyComplete();
//	}
//
//	@Test
//	void testGetUser_notFound() {
//		Mockito.when(repo.findById("1")).thenReturn(Mono.empty());
//		StepVerifier.create(service.getUser("1")).expectError(NotFoundException.class).verify();
//	}
//
//	@Test
//	void testGetByEmail_success() {
//		User user = new User("1", "jay", "jay@mail.com");
//		Mockito.when(repo.findByEmail("jay@mail.com")).thenReturn(Mono.just(user));
//		StepVerifier.create(service.getByEmail("jay@mail.com")).expectNext(user).verifyComplete();
//	}
//
//	@Test
//	void testGetByEmail_notFound() {
//		Mockito.when(repo.findByEmail("x@mail.com")).thenReturn(Mono.empty());
//		StepVerifier.create(service.getByEmail("x@mail.com")).expectError(NotFoundException.class).verify();
//	}
//
//	@Test
//	void testUpdateUser_success() {
//		User existing = new User("1", "Old", "old@mail.com");
//		UserUpdateRequest req = new UserUpdateRequest();
//		req.setName("New");
//		req.setEmail("new@mail.com");
//
//		User updated = new User("1", "New", "new@mail.com");
//		Mockito.when(repo.findById("1")).thenReturn(Mono.just(existing));
//		Mockito.when(repo.save(Mockito.any(User.class))).thenReturn(Mono.just(updated));
//
//		StepVerifier.create(service.updateUser("1", req)).expectNext(updated).verifyComplete();
//	}
//
//	@Test
//	void testUpdateUser_notFound() {
//		Mockito.when(repo.findById("1")).thenReturn(Mono.empty());
//		UserUpdateRequest req = new UserUpdateRequest();
//		req.setName("New");
//		req.setEmail("new@mail.com");
//
//		StepVerifier.create(service.updateUser("1", req)).expectError(NotFoundException.class).verify();
//	}
//
//	@Test
//	void testDeleteUser_success() {
//		User existing = new User("1", "jay", "jay@mail.com");
//		Mockito.when(repo.findById("1")).thenReturn(Mono.just(existing));
//		Mockito.when(repo.delete(existing)).thenReturn(Mono.empty());
//
//		StepVerifier.create(service.deleteUser("1")).verifyComplete();
//	}
//
//	@Test
//	void testDeleteUser_notFound() {
//		Mockito.when(repo.findById("1")).thenReturn(Mono.empty());
//		StepVerifier.create(service.deleteUser("1")).expectError(NotFoundException.class).verify();
//	}
//
//	@Test
//	void testGetAllUsers() {
//		User u1 = new User("1", "jay", "jay@mail.com");
//		User u2 = new User("2", "riya", "riya@mail.com");
//		Mockito.when(repo.findAll()).thenReturn(Flux.just(u1, u2));
//		StepVerifier.create(service.getAllUsers()).expectNext(u1).expectNext(u2).verifyComplete();
//	}
//}
