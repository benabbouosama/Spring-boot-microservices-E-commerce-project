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
                // Routes for microservices
                .route("product_service", r -> r.path("/ecomapi/products/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(productServiceUrl))
                .route("order_service", r -> r.path("/ecomapi/orders/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(orderServiceUrl))
                .route("inventory_service", r -> r.path("/ecomapi/inventory/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(inventoryServiceUrl))
                .route("user_service", r -> r.path("/ecomapi/auth/**")
                        .filters(f -> f.filter(authenticationFilter))
                        .uri(userServiceUrl))

                // Swagger Routes
                .route("product_service_swagger", r -> r.path("/aggregate/product-service/v3/api-docs")
                        .uri(productServiceUrl + "/api-docs"))
                .route("order_service_swagger", r -> r.path("/aggregate/order-service/v3/api-docs")
                        .uri(orderServiceUrl + "/api-docs"))
                .route("inventory_service_swagger", r -> r.path("/aggregate/inventory-service/v3/api-docs")
                        .uri(inventoryServiceUrl + "/api-docs"))
                .route("user_service_swagger", r -> r.path("/aggregate/user-service/v3/api-docs")
                        .uri(userServiceUrl + "/api-docs"))
                .build();
    }
}
