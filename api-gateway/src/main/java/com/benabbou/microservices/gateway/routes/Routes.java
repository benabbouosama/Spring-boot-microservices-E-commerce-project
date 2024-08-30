package com.benabbou.microservices.gateway.routes;

import com.benabbou.microservices.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {

    @Value("${product-service.url}")
    private String productServiceUrl;

    @Value("${order-service.url}")
    private String orderServiceUrl;

    @Value("${inventory-service.url}")
    private String inventoryServiceUrl;

    @Value("${user-service.url}")
    private String userServiceUrl;

    private final AuthenticationFilter authenticationFilter;

    public Routes(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product_service", r -> r.path("/products/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(productServiceUrl))
                .route("order_service", r -> r.path("/orders/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(orderServiceUrl))
                .route("inventory_service", r -> r.path("/inventory/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(inventoryServiceUrl))
                .route("user_service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(userServiceUrl))
                .build();
    }
}
