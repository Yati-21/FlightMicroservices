package com.notification.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.event.BookingCreatedEvent;
import com.notification.service.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCreatedEventListener {

	private final EmailService emailService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@KafkaListener(topics = "booking-created", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
	public void handleBookingCreated(String message) {
		try {
			log.info("Raw message received: {}", message);

			BookingCreatedEvent event = objectMapper.readValue(message, BookingCreatedEvent.class);

			log.info("Converted BookingCreatedEvent: {}", event);

			emailService.sendBookingEmail(event);

		} catch (Exception e) {
			log.error("Error converting message to BookingCreatedEvent: {}", e.getMessage(), e);
		}
	}
}
