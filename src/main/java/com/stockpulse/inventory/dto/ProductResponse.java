package com.stockpulse.inventory.dto;

import com.stockpulse.inventory.domain.Category;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        Category category,
        BigDecimal price,
        Integer stockQuantity,
        Integer reorderThreshold,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getReorderThreshold(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
