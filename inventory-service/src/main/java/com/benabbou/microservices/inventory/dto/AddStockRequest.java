package com.benabbou.microservices.inventory.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddStockRequest {
    private String skuCode;
    private Integer quantity;
}

