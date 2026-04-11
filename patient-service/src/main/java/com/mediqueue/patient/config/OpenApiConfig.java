package com.mediqueue.patient.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global OpenAPI configuration for MediQueue Patient Service.
 *
 * Configures:
 * - API metadata (title, description, version, contact)
 * - JWT Bearer authentication scheme applied globally
 *
 * The aggregated Swagger UI is served via the API Gateway.
 * Individual Swagger UI is disabled in application.yml.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates and configures the OpenAPI bean with API info and security scheme.
     *
     * Security scheme: HTTP Bearer (JWT)
     * - Token is issued by the auth endpoint on login
     * - Must be passed as: Authorization: Bearer <token>
     * - Applied globally — all secured endpoints show a lock icon in Swagger UI
     *
     * @return fully configured OpenAPI instance
     */
    @Bean
    public OpenAPI mediQueueOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MediQueue - Patient Service API")
                        .description("""
                                AI-powered hospital queue management system.
                                
                                This service manages:
                                - User registration and authentication
                                - Patient profiles
                                - Doctor profiles and availability
                                - Appointment booking with AI triage
                                - Queue management
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MediQueue")
                                .email("support@mediqueue.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Provide your JWT token to access secured endpoints")));
    }
}
