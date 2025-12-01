package com.flight.service.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class GlobalErrorHandlerTest {

	private final GlobalErrorHandler handler = new GlobalErrorHandler();

	@Test
	void testHandleNotFound() {
		NotFoundException ex = new NotFoundException("Flight not found");
		Mono<ResponseEntity<Map<String, String>>> result = handler.handleNotFound(ex);

		StepVerifier.create(result).assertNext(response -> {
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
			assertEquals("Flight not found", response.getBody().get("error"));
		}).verifyComplete();
	}

	@Test
	void testHandleBusiness() {
		BusinessException ex = new BusinessException("Invalid details");
		Mono<ResponseEntity<Map<String, String>>> result = handler.handleBusiness(ex);
		StepVerifier.create(result).assertNext(response -> {
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			assertEquals("Invalid details", response.getBody().get("error"));
		}).verifyComplete();
	}

	@Test
	void testHandleValidationErrors() {
		BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "flight");
		binding.addError(new FieldError("flight", "airlineCode", "airlineCode is required"));
		WebExchangeBindException ex = new WebExchangeBindException(null, binding);
		Mono<ResponseEntity<Map<String, Object>>> result = handler.handleValidationErrors(ex);
		StepVerifier.create(result).assertNext(response -> {
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
			Map<String, Object> body = response.getBody();
			assertEquals("Validation failed", body.get("message"));
			assertEquals(400, body.get("status"));
			Map<String, String> errors = (Map<String, String>) body.get("errors");
			assertEquals("airlineCode is required", errors.get("airlineCode"));
		}).verifyComplete();
	}

	@Test
	void testHandleGeneralException() {
		Exception ex = new Exception("Unexpected crash");
		Mono<ResponseEntity<Map<String, String>>> result = handler.handleGeneral(ex);
		StepVerifier.create(result).assertNext(response -> {
			assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
			assertEquals("Unexpected error: Unexpected crash", response.getBody().get("error"));
		}).verifyComplete();
	}
}
