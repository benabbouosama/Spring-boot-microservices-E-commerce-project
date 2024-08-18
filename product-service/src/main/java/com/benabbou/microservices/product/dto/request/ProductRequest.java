package com.benabbou.microservices.product.dto.request;

import java.math.BigDecimal;

public record ProductRequest(String name, String description,
                             String skuCode, BigDecimal price) { }
