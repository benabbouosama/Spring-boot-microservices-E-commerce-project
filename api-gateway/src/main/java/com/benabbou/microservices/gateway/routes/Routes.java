package com.benabbou.microservices.gateway.routes;

import com.benabbou.microservices.gateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RouterFunctions.route;

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
                        .filters(f -> f.filter(authenticationFilter)
                                .circuitBreaker(c -> c.setName("productServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallbackRoute")))
                        .uri(productServiceUrl))
                .route("order_service", r -> r.path("/ecomapi/orders/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .circuitBreaker(c -> c.setName("orderServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallbackRoute")))
                        .uri(orderServiceUrl))
                .route("inventory_service", r -> r.path("/ecomapi/inventory/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .circuitBreaker(c -> c.setName("inventoryServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallbackRoute")))
                        .uri(inventoryServiceUrl))
                .route("user_service", r -> r.path("/ecomapi/auth/**")
                        .filters(f -> f.filter(authenticationFilter)
                                .circuitBreaker(c -> c.setName("userServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallbackRoute")))
                        .uri(userServiceUrl))

                // Swagger Routes
                .route("product_service_swagger", r -> r.path("/aggregate/product-service/v3/api-docs")
                        .filters(f -> f.circuitBreaker(c -> c.setName("productServiceSwaggerCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri(productServiceUrl + "/api-docs"))
                .route("order_service_swagger", r -> r.path("/aggregate/order-service/v3/api-docs")
                        .filters(f -> f.circuitBreaker(c -> c.setName("orderServiceSwaggerCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri(orderServiceUrl + "/api-docs"))
                .route("inventory_service_swagger", r -> r.path("/aggregate/inventory-service/v3/api-docs")
                        .filters(f -> f.circuitBreaker(c -> c.setName("inventoryServiceSwaggerCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri(inventoryServiceUrl + "/api-docs"))
                .route("user_service_swagger", r -> r.path("/aggregate/user-service/v3/api-docs")
                        .filters(f -> f.circuitBreaker(c -> c.setName("userServiceSwaggerCircuitBreaker")
                                .setFallbackUri("forward:/fallbackRoute")))
                        .uri(userServiceUrl + "/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route()
                .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .bodyValue("Service Unavailable. We are currently working to resolve the issue. Please try again later."))
                .build();
    }
}
