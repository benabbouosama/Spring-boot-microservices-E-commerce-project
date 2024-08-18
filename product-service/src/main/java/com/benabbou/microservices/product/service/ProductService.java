package com.benabbou.microservices.product.service;

import com.benabbou.microservices.product.dto.request.ProductRequest;
import com.benabbou.microservices.product.dto.response.ProductResponse;
import com.benabbou.microservices.product.model.Product;
import com.benabbou.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = mapToEntity(productRequest);
        productRepository.save(product);
        log.info("Product created successfully with ID: {}", product.getId());
        return mapToResponse(product);
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new RuntimeException("Product not found");
                });
        return mapToResponse(product);
    }

    public void deleteProductById(String id) {
        if (!productRepository.existsById(id)) {
            log.error("Product with ID: {} not found", id);
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
        log.info("Product with ID: {} deleted successfully", id);
    }

    private Product mapToEntity(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .skuCode(productRequest.skuCode())
                .price(productRequest.price())
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSkuCode(),
                product.getPrice()
        );
    }
}