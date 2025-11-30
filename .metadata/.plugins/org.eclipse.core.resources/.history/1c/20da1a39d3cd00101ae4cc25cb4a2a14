package com.booking.service.request;

import com.booking.service.entity.GENDER;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "gender is required")
    private GENDER gender;

    @Min(value = 1, message = "age must be >=1")
    @Max(value = 120, message = "age must be <=120")
    private int age;

    @Pattern(regexp = "^[A-Z]\\d+$", message = "invalid seat format")
    @NotBlank(message = "seatNumber is required")
    private String seatNumber;
}
