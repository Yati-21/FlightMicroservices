package com.booking.service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced   // allows http://user-service, http://flight-service
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
