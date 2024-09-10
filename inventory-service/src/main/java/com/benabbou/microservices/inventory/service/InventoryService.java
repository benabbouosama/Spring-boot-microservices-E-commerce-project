package com.benabbou.microservices.inventory.service;

import com.benabbou.microservices.inventory.dto.AddStockRequest;
import com.benabbou.microservices.inventory.dto.StockCheckResponse;
import com.benabbou.microservices.inventory.exception.InventoryCheckException;
import com.benabbou.microservices.inventory.feignclient.ProductClient;
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
    private final ProductClient productClient;

    /**
     * Checks the stock availability for a given SKU code and quantity.
     *
     * @param skuCode the SKU code
     * @param quantity the requested quantity
     * @return StockCheckResponse indicating if the product is in stock
     */
    public StockCheckResponse checkStockAvailability(String skuCode, Integer quantity) {
        log.info("Start -- Checking stock for SKU: {}, Requested quantity: {}", skuCode, quantity);
        try {
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

    /**
     * Adds stock to the inventory after verifying the product exists.
     *
     * @param addStockRequest the request containing SKU code and quantity to add
     */
    @Transactional
    public void addStock(AddStockRequest addStockRequest) {
        String skuCode = addStockRequest.getSkuCode();
        Integer quantity = addStockRequest.getQuantity();

        // Verify if the product exists
        if (!productClient.isProductExists(skuCode)) {
            log.error("Product with SKU: {} does not exist", skuCode);
            throw new InventoryCheckException("Product does not exist with SKU: " + skuCode);
        }

        log.info("Adding stock for SKU: {}, Quantity: {}", skuCode, quantity);

        try {
            Inventory existingItem = inventoryRepository.findBySkuCode(skuCode);

            if (existingItem != null) {
                // Update existing item's quantity
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                // Create a new inventory entry
                existingItem = new Inventory();
                existingItem.setSkuCode(skuCode);
                existingItem.setQuantity(quantity);
            }

            // Save the updated or new inventory item
            inventoryRepository.save(existingItem);
            log.info("Stock added for SKU: {}, New quantity: {}", skuCode, existingItem.getQuantity());

        } catch (Exception e) {
            log.error("Error adding stock for SKU: {}, Quantity: {}", skuCode, quantity, e);
            throw new InventoryCheckException("Failed to add stock", e);
        }
    }
}
