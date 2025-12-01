package com.airline.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AirlineUpdateRequest {

	@NotBlank(message = "Airline name is required")
	private String name;
}
