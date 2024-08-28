package com.benabbou.microservices.gateway.routes;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

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

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product_service")
                .route(RequestPredicates.path("/products"), HandlerFunctions.http(productServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order_service")
                .route(RequestPredicates.path("/orders"), HandlerFunctions.http(orderServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory_service")
                .route(RequestPredicates.path("/inventory"), HandlerFunctions.http(inventoryServiceUrl))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return GatewayRouterFunctions.route("user_service")
                .route(RequestPredicates.path("/users"), HandlerFunctions.http(userServiceUrl))
                .build();
    }

}
