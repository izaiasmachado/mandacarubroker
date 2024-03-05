package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.Stock;

public record ResponseStockOwnershipDTO(
    Stock stock,
    int totalShares,
    double positionValue
) {
    public static ResponseStockOwnershipDTO fromStockPosition(final StockOwnership stockPosition) {
        final Stock stock = stockPosition.getStock();

        return new ResponseStockOwnershipDTO(
            stock,
            stockPosition.getShares(),
            stockPosition.getTotalValue()
        );
    }

    public static ResponseStockOwnershipDTO fromStock(final Stock stock) {
        return new ResponseStockOwnershipDTO(
            stock,
            0,
            0
        );
    }
}
