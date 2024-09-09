package com.benabbou.microservices.inventory.service;

import com.benabbou.microservices.inventory.dto.AddStockRequest;
import com.benabbou.microservices.inventory.dto.StockCheckResponse;
import com.benabbou.microservices.inventory.exception.InventoryCheckException;
import com.benabbou.microservices.inventory.model.Inventory;
import com.benabbou.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Method to check stock availability
    public StockCheckResponse checkStockAvailability(String skuCode, Integer quantity) {
        log.info("Start -- Checking stock for SKU: {}, Requested quantity: {}", skuCode, quantity);
        try {
            // Fetch inventory once to avoid redundant queries
            Inventory inventory = inventoryRepository.findBySkuCode(skuCode);
            boolean isInStock = inventory != null && inventory.getQuantity() >= quantity;
            Integer availableQuantity = inventory != null ? inventory.getQuantity() : 0;
            log.info("End -- SKU: {}, Requested quantity: {}, In stock: {}", skuCode, quantity, isInStock);
            return new StockCheckResponse(isInStock, skuCode, quantity, availableQuantity);
        } catch (Exception e) {
            log.error("Error checking stock for SKU: {}, Requested quantity: {}", skuCode, quantity, e);
            throw new InventoryCheckException("Unable to check stock availability", e);
        }
    }

    // Method to add stock to the inventory
    @Transactional
    public void addStock(AddStockRequest addStockRequest) {
        log.info("Adding stock for SKU: {}, Quantity: {}", addStockRequest.getSkuCode(), addStockRequest.getQuantity());

        try {
            Inventory existingItem = inventoryRepository.findBySkuCode(addStockRequest.getSkuCode());

            if (existingItem != null) {
                // If the item exists, update its quantity
                existingItem.setQuantity(existingItem.getQuantity() + addStockRequest.getQuantity());
            } else {
                // If it doesn't exist, create a new entry
                existingItem = new Inventory();
                existingItem.setSkuCode(addStockRequest.getSkuCode());
                existingItem.setQuantity(addStockRequest.getQuantity());
            }

            // Save the updated or new inventory item
            inventoryRepository.save(existingItem);
            log.info("Stock added for SKU: {}, New quantity: {}", addStockRequest.getSkuCode(), existingItem.getQuantity());

        } catch (Exception e) {
            log.error("Error adding stock for SKU: {}, Quantity: {}", addStockRequest.getSkuCode(), addStockRequest.getQuantity(), e);
            throw new InventoryCheckException("Failed to add stock", e);
        }
    }
}
