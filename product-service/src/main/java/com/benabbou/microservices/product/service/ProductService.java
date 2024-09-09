package com.benabbou.microservices.product.service;

import com.benabbou.microservices.product.dto.request.ProductRequest;
import com.benabbou.microservices.product.dto.response.ProductResponse;
import com.benabbou.microservices.product.model.Product;
import com.benabbou.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Creates a new product and returns a response.
     *
     * @param productRequest the product request DTO
     * @return ProductResponse with the created product details
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = mapToEntity(productRequest);
        productRepository.save(product);
        log.info("Product created successfully with ID: {}", product.getId());
        return mapToResponse(product);
    }

    /**
     * Retrieves all products.
     *
     * @return List of ProductResponse
     */
    @Transactional(readOnly = true) // Transaction is read-only for performance optimization
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the product ID
     * @return ProductResponse
     */
    @Transactional(readOnly = true) // Read-only transaction since it's a retrieval
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found", id);
                    return new RuntimeException("Product not found");
                });
        return mapToResponse(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the product ID
     */
    @Transactional
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
