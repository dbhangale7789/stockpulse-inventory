package com.stockpulse.inventory.repository;

import com.stockpulse.inventory.domain.Category;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySku(String sku);

    List<Product> findByCategory(Category category);

    List<Product> findByStatus(ProductStatus status);
}
