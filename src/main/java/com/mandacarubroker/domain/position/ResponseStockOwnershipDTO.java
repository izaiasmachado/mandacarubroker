package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;

public record ResponseStockOwnershipDTO(
        ResponseStockDTO stock,
        int totalShares,
        double positionValue) {
    public static ResponseStockOwnershipDTO fromStockOwnership(final StockOwnership stockOwnership) {
        final Stock stock = stockOwnership.getStock();

        return new ResponseStockOwnershipDTO(
                ResponseStockDTO.fromStock(stock),
                stockOwnership.getShares(),
                stockOwnership.getTotalValue());
    }

    public static ResponseStockOwnershipDTO fromStock(final Stock stock) {
        return new ResponseStockOwnershipDTO(
                ResponseStockDTO.fromStock(stock),
                0,
                0);
    }
}
