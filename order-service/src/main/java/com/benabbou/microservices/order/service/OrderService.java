package com.benabbou.microservices.order.service;

import com.benabbou.microservices.order.dto.OrderRequest;
import com.benabbou.microservices.order.model.Order;
import com.benabbou.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public String placeOrder(OrderRequest orderRequest) {
        try {
            // Create a new order object
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setPrice(orderRequest.price().multiply(BigDecimal.valueOf(orderRequest.quantity())));
            order.setSkuCode(orderRequest.skuCode());
            order.setQuantity(orderRequest.quantity());

            // Save the order to the repository
            Order savedOrder = orderRepository.save(order);

            // Log the successful order placement
            log.info("Order placed successfully with ID: {}", savedOrder.getId());

            // Return the order Number
            return savedOrder.getOrderNumber();

        } catch (Exception e) {
            // Log the exception and rethrow or handle as needed
            log.error("Error placing order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to place the order due to: " + e.getMessage());
        }
    }
}
