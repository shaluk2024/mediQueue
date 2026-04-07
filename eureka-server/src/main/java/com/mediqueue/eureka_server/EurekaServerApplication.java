package com.mediqueue.eureka_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Main class for Eureka Discovery Server
 *
 * This service acts as a registry where all microservices
 * (like patient-service, doctor-service, etc.) will register themselves.
 *
 * Other services can discover each other using this registry
 * instead of hardcoding URLs.
 */
@SpringBootApplication  // Marks this as a Spring Boot application (auto configuration + component scan)
@EnableEurekaServer     // Enables Eureka Server (service registry)
public class EurekaServerApplication {

	/**
	 * Entry point of the application
	 *
	 * Starts the Spring Boot application and initializes
	 * the Eureka server on configured port (default: 8761)
	 */
	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}

}
