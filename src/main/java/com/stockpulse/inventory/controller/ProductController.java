package com.stockpulse.inventory.controller;

import com.stockpulse.inventory.domain.Category;
import com.stockpulse.inventory.domain.ProductStatus;
import com.stockpulse.inventory.dto.ProductRequest;
import com.stockpulse.inventory.dto.ProductResponse;
import com.stockpulse.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) ProductStatus status) {
        return productService.getAllProducts(category, status);
    }

    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @PatchMapping("/{id}/stock")
    public ProductResponse adjustStock(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer quantity = payload.get("quantity");
        return productService.setStock(id, quantity);
    }

    @PatchMapping("/{id}/stock/delta")
    public ProductResponse updateStockByDelta(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer delta = payload.get("delta");
        return productService.adjustStock(id, delta);
    }

    @PostMapping("/{id}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse processOrder(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        Integer quantity = payload.get("quantity");
        return productService.processOrder(id, quantity);
    }

    @PatchMapping("/{id}/price")
    public ProductResponse updatePrice(@PathVariable Long id, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal newPrice = payload.get("price");
        return productService.updatePrice(id, newPrice);
    }
}
