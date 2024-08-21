package com.benabbou.microservices.order.controller;

import com.benabbou.microservices.order.dto.OrderRequest;
import com.benabbou.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        try {
            // Call the service to place the order and get the order Number
            String orderNumber = orderService.placeOrder(orderRequest);

            // Log the successful order placement
            log.info("Order placed successfully with Number: {}", orderNumber);

            // Return a response with the order Number and a success message
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Order placed successfully. Order Number: " + orderNumber);

        } catch (Exception e) {
            // Log the error and return an appropriate response
            log.error("Error placing order: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to place the order. Please try again later.");
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("An error occurred: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
    }

    @GetMapping("/fallback")
    public CompletableFuture<ResponseEntity<String>> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        // Log the fallback occurrence
        log.warn("Fallback method triggered due to: {}", runtimeException.getMessage());

        // Return a fallback response
        return CompletableFuture.supplyAsync(() ->
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Oops! Something went wrong. Please try ordering again later!")
        );
    }
}