package com.passenger.service.request;

import com.passenger.service.entity.GENDER;

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

    @Pattern(regexp = "^[A-Z]\\d+$", message = "Invalid seat format")
    private String seatNumber;

    @NotBlank(message = "bookingId is required")
    private String bookingId;
}
