package com.benabbou.microservices.gateway.filter;

import com.benabbou.microservices.gateway.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter implements GatewayFilter {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = null;
        String path = exchange.getRequest().getPath().toString();
        log.debug("Processing request for path: {}", path);

        if (validator.isSecured.test(exchange.getRequest())) {
            log.debug("Request to path '{}' is secured, checking authorization header", path);

            HttpHeaders headers = exchange.getRequest().getHeaders();
            if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing authorization header for request to path '{}'", path);
                return unauthorizedResponse(exchange, "Missing authorization header");
            }

            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
                log.debug("Extracted token from authorization header for path '{}'", path);
            } else {
                log.warn("Invalid authorization header format for request to path '{}'", path);
                return unauthorizedResponse(exchange, "Invalid authorization header format");
            }

            try {
                jwtUtil.validateToken(authHeader);
                log.debug("Token validation successful for path '{}'", path);
                request =  exchange.getRequest()
                        .mutate()
                        .header("loggedInUserName" , jwtUtil.extractUsername(authHeader))
                        .header("loggedInEmail" , jwtUtil.extractEmail(authHeader))
                        .build();


            } catch (Exception e) {
                log.error("Token validation failed for path '{}': {}", path, e.getMessage(), e);
                return unauthorizedResponse(exchange, "Unauthorized access");
            }
        } else {
            log.debug("Request to path '{}' is not secured, skipping authorization check", path);
        }

        return chain.filter(exchange.mutate().request(request).build())
                .doOnSuccess(aVoid -> log.debug("Request to path '{}' processed successfully", path))
                .doOnError(throwable -> log.error("Error processing request to path '{}': {}", path, throwable.getMessage(), throwable));
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        log.info("Unauthorized access: {}", message);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
