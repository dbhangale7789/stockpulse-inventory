package com.stockpulse.inventory.service.ai;

import java.math.BigDecimal;

public final class AiResponseValidator {

    private AiResponseValidator() {
    }

    public static boolean isValidConfidence(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("1.00")) <= 0;
    }

    public static boolean isValidPrice(BigDecimal price, BigDecimal currentPrice) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (currentPrice == null) {
            return true;
        }
        BigDecimal maxAllowed = currentPrice.multiply(new BigDecimal("10"));
        return price.compareTo(maxAllowed) <= 0;
    }

    public static boolean isValidQuantity(Integer quantity) {
        return quantity != null && quantity > 0;
    }
}
