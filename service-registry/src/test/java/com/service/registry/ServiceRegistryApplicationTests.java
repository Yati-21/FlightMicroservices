package com.service.registry;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServiceRegistryApplicationTests {

	@Test
	void contextLoads() {
		// Spring Boot test auto-fails if context fails to load
        assertTrue(true);
	}

}
