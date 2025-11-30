package com.booking.service.dto;

import com.booking.service.entity.GENDER;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PassengerCreateRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "gender is required")
    private GENDER gender;

    @Min(1)
    @Max(120)
    private int age;

    @Pattern(regexp = "^[A-Z]\\d+$", message = "invalid seat format")
    @NotBlank(message = "seatNumber is required")
    private String seatNumber;

    @NotBlank(message = "bookingId is required")
    private String bookingId;
}
