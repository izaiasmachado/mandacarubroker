package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.Stock;

public record ResponseStockOwnershipDTO(
    String id,
    Stock stock,
    int totalShares,
    double positionValue
) {
    public static ResponseStockOwnershipDTO fromStockPosition(final StockOwnership stockPosition) {
        final Stock stock = stockPosition.getStock();

        return new ResponseStockOwnershipDTO(
            stockPosition.getId(),
            stock,
            stockPosition.getShares(),
            stockPosition.getTotalValue()
        );
    }
}
