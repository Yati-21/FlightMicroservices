package com.passenger.service.exception;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalErrorHandler {

    private static final String ERROR = "error";

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationErrors(WebExchangeBindException ex) {

        List<FieldError> errors = ex.getFieldErrors();
        Map<String, String> errorMap = errors.stream().collect(
                Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)
        );

        Map<String, Object> body = Map.of(
                "errors", errorMap,
                "message", "Validation failed",
                "status", 400
        );

        return Mono.just(ResponseEntity.badRequest().body(body));
    }

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleNotFound(NotFoundException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGeneral(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(ERROR, ex.getMessage())));
    }
}
