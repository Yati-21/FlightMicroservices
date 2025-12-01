package com.booking.service.dto;

import java.time.LocalDateTime;

import com.booking.service.entity.AIRPORT_CODE;
import com.booking.service.entity.FLIGHT_STATUS;

import lombok.Data;

@Data
public class FlightDto {

	private String id;
	private String airlineCode;
	private String flightNumber;
	private AIRPORT_CODE fromCity;
	private AIRPORT_CODE toCity;
	private LocalDateTime departureTime;
	private LocalDateTime arrivalTime;
	private int totalSeats;
	private int availableSeats;
	private float price;
	private FLIGHT_STATUS status;
}
