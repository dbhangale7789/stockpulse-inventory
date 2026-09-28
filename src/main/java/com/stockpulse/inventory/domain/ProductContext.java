package com.stockpulse.inventory.domain;

import java.math.BigDecimal;

public class ProductContext {
    private final Product product;
    private final Long productId;
    private final BigDecimal categoryAveragePrice;
    private final double demandVelocity;
    private final double categoryAverageDemandVelocity;
    private final TriggerReason triggerReason;

    public ProductContext(Product product,
                          long productId,
                          BigDecimal categoryAveragePrice,
                          double demandVelocity,
                          double categoryAverageDemandVelocity,
                          TriggerReason triggerReason) {
        this.product = product;
        this.productId = productId;
        this.categoryAveragePrice = categoryAveragePrice;
        this.demandVelocity = demandVelocity;
        this.categoryAverageDemandVelocity = categoryAverageDemandVelocity;
        this.triggerReason = triggerReason;
    }

    public Product getProduct() { return product; }
    public Long getProductId() { return productId; }
    public BigDecimal getCategoryAveragePrice() { return categoryAveragePrice; }
    public double getDemandVelocity() { return demandVelocity; }
    public double getCategoryAverageDemandVelocity() { return categoryAverageDemandVelocity; }
    public TriggerReason getTriggerReason() { return triggerReason; }
}
