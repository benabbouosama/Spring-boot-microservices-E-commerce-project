package com.benabbou.microservices.order.feignclient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory", url = "${inventory.url}")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/ecomapi/inventory")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    @TimeLimiter(name = "inventory")
    boolean isInStock(@RequestParam("skuCode") String skuCode, @RequestParam("quantity") Integer quantity);

    Logger logger = LoggerFactory.getLogger(InventoryClient.class);

    default boolean fallbackMethod(String skuCode, Integer quantity, Throwable throwable) {
        logger.error("Fallback method called due to an error while checking inventory for SKU: {} and Quantity: {}. Error: {}", skuCode, quantity, throwable.getMessage());
        return false; // Default response in case of failure
    }
}
