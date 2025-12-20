package com.gymcrm.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gym CRM REST API",
                version = "1.0",
                description = "REST API for Gym Customer Relationship Management System",
                contact = @Contact(
                        name = "Gym CRM Team",
                        email = "dilnuraliqulov@gmail.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
        }
)
public class OpenApiConfig {
}

