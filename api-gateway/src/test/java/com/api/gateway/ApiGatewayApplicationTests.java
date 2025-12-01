package com.api.gateway;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
		// spring boot test auto-fails if context fails to load
		assertTrue(true);
	}

}
