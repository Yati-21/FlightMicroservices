package com.notification.service.service;

import com.notification.service.event.BookingCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailServiceTest {

	@Test
	void testSendBookingEmail() {

		JavaMailSender mailSender = mock(JavaMailSender.class);

		EmailService emailService = new EmailService(mailSender);

		BookingCreatedEvent event = new BookingCreatedEvent("PNR123", "test@gmail.com", "John", "FL101", 2,
				"Booking successful!");

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

		emailService.sendBookingEmail(event);

		verify(mailSender, times(1)).send(captor.capture());

		SimpleMailMessage sentMessage = captor.getValue();

		assertEquals("test@gmail.com", sentMessage.getTo()[0]);
		assertEquals("Booking Confirmation - PNR PNR123", sentMessage.getSubject());
		assertEquals(true, sentMessage.getText().contains("PNR123"));
	}
}
