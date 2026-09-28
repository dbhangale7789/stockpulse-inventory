package com.stockpulse.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductOrderRequest(
        @NotNull @Min(1) Integer quantity,
        String note
) {
}
