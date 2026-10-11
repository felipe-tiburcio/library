package com.ftatech.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bibliotecaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca API")
                        .version("v1")
                        .description("REST API for managing books, users, loans, reservations, and reports."));
    }
}
