package com.passenger.service.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {

		List<FieldError> errors = ex.getFieldErrors();

		Map<String, String> fieldMap = errors.stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a + "; " + b));

		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(Map.of("errors", fieldMap, "message", "Validation failed", "status", 400)));
	}

	@ExceptionHandler(NotFoundException.class)
	public Mono<ResponseEntity<Map<String, String>>> handleNotFound(NotFoundException ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage())));
	}

	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<Map<String, String>>> handleGeneral(Exception ex) {
		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", ex.getMessage() == null ? "Unexpected error" : ex.getMessage())));
	}
}
