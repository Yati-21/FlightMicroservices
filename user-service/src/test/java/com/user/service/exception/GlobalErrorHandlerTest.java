package com.user.service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorHandlerTest {

    GlobalErrorHandler handler = new GlobalErrorHandler();

    // ----------------------------------------------------
    // 1. VALIDATION HANDLER TEST
    // ----------------------------------------------------
    @Test
    void testHandleValidation() {

        BeanPropertyBindingResult result =
                new BeanPropertyBindingResult(new Object(), "obj");

        // Add some validation errors
        result.addError(new FieldError("obj", "name", "Name is required"));
        result.addError(new FieldError("obj", "email", "Email invalid"));

        WebExchangeBindException ex = new WebExchangeBindException(null, result);

        StepVerifier.create(handler.handleValidation(ex))
                .assertNext(response -> {

                    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

                    Map<String, Object> body = response.getBody();
                    assertNotNull(body);

                    // errors map
                    Map<String, String> errors = (Map<String, String>) body.get("errors");
                    assertEquals("Name is required", errors.get("name"));
                    assertEquals("Email invalid", errors.get("email"));

                    assertEquals("Validation failed", body.get("message"));
                })
                .verifyComplete();
    }

    // ----------------------------------------------------
    // 2. NOT FOUND HANDLER TEST
    // ----------------------------------------------------
    @Test
    void testHandleNotFound() {

        NotFoundException ex = new NotFoundException("User not found");

        StepVerifier.create(handler.handleNotFound(ex))
                .assertNext(response -> {

                    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                    assertEquals("User not found", response.getBody().get("error"));
                })
                .verifyComplete();
    }

    // ----------------------------------------------------
    // 3. GENERAL EXCEPTION HANDLER TEST
    // ----------------------------------------------------
    @Test
    void testHandleGeneral() {

        Exception ex = new Exception("Something failed");

        StepVerifier.create(handler.handleGeneral(ex))
                .assertNext(response -> {

                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                    assertEquals("Something failed", response.getBody().get("error"));
                })
                .verifyComplete();
    }
}
