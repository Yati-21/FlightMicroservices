package com.notification.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent 
{
	//for notification service
    private String pnr;
    private String message;
    private String userName;
    private String userEmail;
    private String flightId;
    private int seatsBooked;
}
