package com.airline.service.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

    private static final String ERROR = "error";

    // validation errors (@Valid)
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {

        List<FieldError> fieldErrors = ex.getFieldErrors();

        Map<String, String> errorMap = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing + "; " + replacement
                ));

        Map<String, Object> body = Map.of(
                "errors", errorMap,
                "message", "Validation failed",
                "status", 400
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body));
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleNotFound(NotFoundException ex) {
        Map<String, String> error = Map.of(ERROR, ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneral(Exception ex) {
        String raw = (ex.getMessage() == null ? "" : ex.getMessage())
                .trim()
                .replace("\n", " ")
                .replace("\r", " ");
        Map<String, String> error = Map.of(ERROR, "Unexpected error: " + raw);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
