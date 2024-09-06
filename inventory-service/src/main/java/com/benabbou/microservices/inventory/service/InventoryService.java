package com.benabbou.microservices.inventory.service;

import com.benabbou.microservices.inventory.dto.AddStockRequest;
import com.benabbou.microservices.inventory.dto.StockCheckResponse;
import com.benabbou.microservices.inventory.exception.InventoryCheckException;
import com.benabbou.microservices.inventory.model.Inventory;
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


    // Method to add stock to the inventory
    public void addStock(AddStockRequest addStockRequest) {
        log.info("Adding stock for SKU: {}, Quantity: {}", addStockRequest.getSkuCode(), addStockRequest.getQuantity());

        Inventory existingItem = inventoryRepository.findBySkuCode(addStockRequest.getSkuCode());
        if (existingItem != null) {
            // If the item already exists, increase its quantity
            existingItem.setQuantity(existingItem.getQuantity() + addStockRequest.getQuantity());
        } else {
            // If the item doesn't exist, create a new entry
            existingItem = new Inventory();
            existingItem.setSkuCode(addStockRequest.getSkuCode());
            existingItem.setQuantity(addStockRequest.getQuantity());
        }

        inventoryRepository.save(existingItem);
        log.info("Stock added for SKU: {}, New quantity: {}", addStockRequest.getSkuCode(), existingItem.getQuantity());
    }
}
