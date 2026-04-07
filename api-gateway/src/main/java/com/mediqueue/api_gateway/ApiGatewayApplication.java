package com.mediqueue.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main class for API Gateway
 *
 * This service acts as a single entry point for all client requests.
 * It routes requests to appropriate microservices using service discovery (Eureka).
 *
 * Responsibilities:
 * ✅ Route requests to microservices
 * ✅ Apply filters (authentication, logging, etc.)
 * ✅ Centralize cross-cutting concerns
 */
@SpringBootApplication  // Enables Spring Boot auto-configuration and component scanning
@EnableDiscoveryClient  // Registers this service with Eureka and enables service discovery
public class ApiGatewayApplication {

	/**
	 * Entry point of the API Gateway application
	 *
	 * Starts the Spring Boot application on configured port (e.g., 8080)
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
