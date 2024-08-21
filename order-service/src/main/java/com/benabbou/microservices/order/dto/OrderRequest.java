package com.benabbou.microservices.order.dto;

import java.math.BigDecimal;

public record OrderRequest(
        Long orderNumber,
        String skuCode,
        BigDecimal price,
        Integer quantity,
        UserDetails userDetails
) {

    public record UserDetails(
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            String country
    ) {}
}
