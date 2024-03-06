package com.mandacarubroker.domain.position;

import jakarta.validation.constraints.Positive;

public record RequestStockOwnershipDTO(
        @Positive(message = "Share amount must be greater than zero")
        int shares
) {
}
