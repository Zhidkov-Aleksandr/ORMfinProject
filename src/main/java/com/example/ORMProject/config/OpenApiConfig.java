package com.example.ORMProject.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Базовая конфигурация OpenAPI/Swagger UI. Swagger-UI по адресу: /swagger-ui.html

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI educationPlatformOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Учебная платформа API")
                        .description("REST API для управления курсами, заданиями и тестами")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}
