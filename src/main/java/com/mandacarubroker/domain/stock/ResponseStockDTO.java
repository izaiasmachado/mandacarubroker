package com.mandacarubroker.domain.stock;

public record ResponseStockDTO(
        String id,
        String symbol,
        String companyName,
        double price
) {
    public static ResponseStockDTO fromStock(final Stock stock) {
        return new ResponseStockDTO(
                stock.getId(),
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getPrice()
        );
    }
}
