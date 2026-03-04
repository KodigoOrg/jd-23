package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Productos API")
                        .description("API REST para gestión de productos — Práctica Sesión 2")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Spring Boot Professional")
                                .email("contacto@springpro.com")));
    }
}
