package com.notification.service.kafka;

import com.notification.service.event.BookingCreatedEvent;
import com.notification.service.service.EmailService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class BookingCreatedEventListenerTest {

	@Test
	void testHandleBookingCreated() throws Exception {
		EmailService emailService = mock(EmailService.class);
		BookingCreatedEventListener listener = new BookingCreatedEventListener(emailService);
		String jsonMessage = """
				{
				  "pnr":"PNR555",
				  "userEmail":"abc@test.com",
				  "userName":"Yati",
				  "flightId":"FL001",
				  "seatsBooked":2,
				  "message":"Booking successful!"
				}
				""";
		listener.handleBookingCreated(jsonMessage);
		verify(emailService, times(1)).sendBookingEmail(any(BookingCreatedEvent.class));
	}
}
