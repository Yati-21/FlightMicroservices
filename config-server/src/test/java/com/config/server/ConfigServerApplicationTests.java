package com.config.server;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ConfigServerApplicationTests {

	@Test
	void contextLoads() {
		// Spring Boot test auto-fails if context fails to load
        assertTrue(true);
	}

}
