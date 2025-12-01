package com.notification.service.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class KafkaListenerConfigTest {

	@Autowired
	private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;

	@Test
	void testKafkaListenerFactoryBeanLoads() {
		assertNotNull(kafkaListenerContainerFactory);
	}
}
