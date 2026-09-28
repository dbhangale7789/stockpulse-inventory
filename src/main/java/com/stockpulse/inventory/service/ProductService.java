package com.stockpulse.inventory.service;

import com.stockpulse.inventory.domain.Category;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductStatus;
import com.stockpulse.inventory.dto.ProductRequest;
import com.stockpulse.inventory.dto.ProductResponse;
import com.stockpulse.inventory.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> getAllProducts(Category category, ProductStatus status) {
        Specification<Product> spec = Specification.where(null);

        if (category != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), category));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return productRepository.findAll(spec).stream()
                .map(ProductResponse::from)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse createProduct(@Valid ProductRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new IllegalArgumentException("Product with sku already exists: " + request.sku());
        }

        Product product = new Product(
                request.sku(),
                request.name(),
                request.category(),
                request.price(),
                request.stockQuantity(),
                request.reorderThreshold()
        );
        product.setStatus(product.resolveStatus(product.getStockQuantity()));

        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        product.setSku(request.sku());
        product.setName(request.name());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setReorderThreshold(request.reorderThreshold());
        product.setStatus(product.resolveStatus(product.getStockQuantity()));

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse adjustStock(Long id, Integer delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Stock adjustment quantity is required");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        int updatedStock = product.getStockQuantity() + delta;
        if (updatedStock < 0) {
            throw new IllegalArgumentException("Stock cannot go below zero");
        }

        product.setStockQuantity(updatedStock);
        product.setStatus(product.resolveStatus(product.getStockQuantity()));
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse setStock(Long id, Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Stock quantity must be a non-negative integer");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        product.setStockQuantity(quantity);
        product.setStatus(product.resolveStatus(quantity));
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse processOrder(Long id, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be a positive integer");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        int remainingStock = product.getStockQuantity() - quantity;
        if (remainingStock < 0) {
            throw new IllegalArgumentException("Order quantity exceeds available stock");
        }

        product.setStockQuantity(remainingStock);
        product.setStatus(product.resolveStatus(remainingStock));
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updatePrice(Long id, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));

        product.setPrice(newPrice);
        return ProductResponse.from(productRepository.save(product));
    }
}
