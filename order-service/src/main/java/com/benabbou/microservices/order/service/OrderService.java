package com.benabbou.microservices.order.service;

import com.benabbou.microservices.order.dto.OrderRequest;
import com.benabbou.microservices.order.feignclient.InventoryClient;
import com.benabbou.microservices.order.feignclient.UserClient;
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
    private final InventoryClient inventoryClient;
    private final UserClient userClient;

    public String placeOrder(OrderRequest orderRequest) {
        try {
            // Check if the user exists
            boolean userExists = userClient.doesUserExist(orderRequest.userDetails().email());
            if (!userExists) {
                throw new RuntimeException("User with email " + orderRequest.userDetails().email() + " does not exist");
            }

            // Check if the product is in stock
            boolean isInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());
            if (isInStock) {
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

                // Return the order number
                return savedOrder.getOrderNumber();
            } else {
                throw new RuntimeException("Product with SkuCode " + orderRequest.skuCode() + " is not in stock");
            }

        } catch (Exception e) {
            // Log the exception and rethrow or handle as needed
            log.error("Error placing order: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to place the order due to: " + e.getMessage());
        }
    }
}
