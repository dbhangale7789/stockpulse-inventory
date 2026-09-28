package com.stockpulse.inventory;

import com.stockpulse.inventory.domain.Category;
import com.stockpulse.inventory.domain.Product;
import com.stockpulse.inventory.domain.ProductStatus;
import com.stockpulse.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        Product product = new Product("SKU-100", "Wireless Mouse", Category.ELECTRONICS, new BigDecimal("29.99"), 25, 10);
        productRepository.save(product);
    }

    @Test
    void shouldCreateAndListProducts() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"SKU-200\",\"name\":\"Mechanical Keyboard\",\"category\":\"ELECTRONICS\",\"price\":\"89.99\",\"stockQuantity\":45,\"reorderThreshold\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sku").value("SKU-200"));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldUpdateStockAndStatus() throws Exception {
        Product product = productRepository.findAll().getFirst();

        mockMvc.perform(patch("/api/products/" + product.getId() + "/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\": 8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(8))
                .andExpect(jsonPath("$.status").value(ProductStatus.LOW_STOCK.name()));
    }
}
