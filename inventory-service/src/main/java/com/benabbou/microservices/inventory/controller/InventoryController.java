package com.benabbou.microservices.inventory.controller;

import com.benabbou.microservices.inventory.dto.AddStockRequest;
import com.benabbou.microservices.inventory.dto.StockCheckResponse;
import com.benabbou.microservices.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ecomapi/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Checks if the product is in stock for the given SKU code and quantity.
     *
     * @param skuCode the SKU code
     * @param quantity the requested quantity
     * @return boolean indicating if the product is in stock
     */
    @GetMapping("/check-stock")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        return inventoryService.checkStockAvailability(skuCode, quantity).isInStock();
    }

    /**
     * Adds new stock to the inventory.
     *
     * @param addStockRequest the request containing SKU code and quantity to add
     * @return a confirmation message
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public String addStock(@RequestBody AddStockRequest addStockRequest) {
        inventoryService.addStock(addStockRequest);
        return "Stock added successfully!";
    }
}
