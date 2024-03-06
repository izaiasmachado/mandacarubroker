package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;

public record ResponseStockOwnershipDTO(
        ResponseStockDTO stock,
        int totalShares,
        double positionValue) {
    public static ResponseStockOwnershipDTO fromStockPosition(final StockOwnership stockPosition) {
        final Stock stock = stockPosition.getStock();

        return new ResponseStockOwnershipDTO(
                ResponseStockDTO.fromStock(stock),
                stockPosition.getShares(),
                stockPosition.getTotalValue());
    }

    public static ResponseStockOwnershipDTO fromStock(final Stock stock) {
        return new ResponseStockOwnershipDTO(
                ResponseStockDTO.fromStock(stock),
                0,
                0);
    }
}
