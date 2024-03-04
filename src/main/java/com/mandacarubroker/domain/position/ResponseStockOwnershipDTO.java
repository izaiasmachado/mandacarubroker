package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;

public record ResponseStockOwnershipDTO(
    String id,
    ResponseStockDTO stock,
    int totalShares,
    double positionValue
) {
    public static ResponseStockOwnershipDTO fromStockPosition(final StockOwnership stockPosition) {
        final Stock stock = stockPosition.getStock();

        return new ResponseStockOwnershipDTO(
            stockPosition.getId(),
            ResponseStockDTO.fromStock(stock),
            stockPosition.getShares(),
            stockPosition.getTotalValue()
        );
    }
}
