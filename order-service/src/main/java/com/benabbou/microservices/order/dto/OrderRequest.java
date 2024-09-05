package com.benabbou.microservices.order.dto;

import java.math.BigDecimal;

public record OrderRequest(
        Long orderNumber,
        String skuCode,
        BigDecimal price,
        Integer quantity
) {
}
