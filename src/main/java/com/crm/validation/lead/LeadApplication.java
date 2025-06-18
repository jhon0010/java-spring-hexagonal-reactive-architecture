package com.crm.validation.lead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Lead Validation Service.
 * This class serves as the entry point for the Spring Boot application.
 *
 * TODO:
 * - Consider use of domain events for better decoupling.
 */
@SpringBootApplication
public class LeadApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadApplication.class, args);
	}
}
