package com.benabbou.microservices.order.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI orderServiceAPI() {
        return new OpenAPI()
                .info(new Info().title("Order Service API")
                        .description("REST API for managing orders in the system")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Find additional resources and documentation for the Order Service API"));
    }
}
