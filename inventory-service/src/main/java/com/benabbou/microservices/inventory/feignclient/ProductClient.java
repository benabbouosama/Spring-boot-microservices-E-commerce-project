package com.benabbou.microservices.inventory.feignclient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${product.service.url}")
public interface ProductClient {

    @GetMapping("/ecomapi/products/exists/sku/{skuCode}")
    @CircuitBreaker(name = "productServiceCircuitBreaker", fallbackMethod = "fallbackIsProductExists")
    @Retry(name = "productServiceRetry")
    boolean isProductExists(@PathVariable("skuCode") String skuCode);

    Logger logger = LoggerFactory.getLogger(ProductClient.class);

    default boolean fallbackIsProductExists(String skuCode, Throwable throwable) {
        logger.error("Fallback method called due to an error while checking product existence for SKU: {}. Error: {}", skuCode, throwable.getMessage());
        return false;
    }
}
