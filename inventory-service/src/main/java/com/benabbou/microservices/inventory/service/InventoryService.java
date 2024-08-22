package com.benabbou.microservices.inventory.service;

import com.benabbou.microservices.inventory.dto.StockCheckResponse;
import com.benabbou.microservices.inventory.exception.InventoryCheckException;
import com.benabbou.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public StockCheckResponse checkStockAvailability(String skuCode, Integer quantity) {
        log.info("Start -- Checking stock for SKU: {}, Requested quantity: {}", skuCode, quantity);
        try {
            boolean isInStock = inventoryRepository.existsAndQuantityIsGreaterThanEqual(skuCode, quantity);
            Integer availableQuantity = isInStock ? inventoryRepository.findBySkuCode(skuCode).getQuantity() : 0;
            log.info("End -- SKU: {}, Requested quantity: {}, In stock: {}", skuCode, quantity, isInStock);
            return new StockCheckResponse(isInStock, skuCode, quantity, availableQuantity);
        } catch (Exception e) {
            log.error("Error checking stock for SKU: {}, Requested quantity: {}", skuCode, quantity, e);
            throw new InventoryCheckException("Unable to check stock availability", e);
        }
    }
}
