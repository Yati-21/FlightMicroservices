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
    private String userEmail;
    private String userName;
    private String flightId;
    private int seatsBooked;
    private String message;
}
