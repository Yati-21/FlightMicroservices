package com.notification.service.service;

import com.notification.service.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	public void sendBookingEmail(BookingCreatedEvent event) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(event.getUserEmail());
		message.setSubject("Booking Confirmation - PNR " + event.getPnr());
		message.setText("Hello " + event.getUserName() + ",\n\n" + "Your booking has been successfully created.\n"
				+ "PNR: " + event.getPnr() + "\n" + "Flight: " + event.getFlightId() + "\n" + "Seats Booked: "
				+ event.getSeatsBooked() + "\n\n" + "Message: " + event.getMessage() + "\n\n"
				+ "Thank you for booking with us!");

		mailSender.send(message);
		log.info("Email sent to {}", event.getUserEmail());
	}
}
