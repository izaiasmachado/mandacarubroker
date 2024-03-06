package com.mandacarubroker.domain.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class StockUtils {
    private StockUtils() {
    }

    public static void assertStocksAreEqual(final Stock expectedStock, final Stock actualStock) {
        assertEquals(expectedStock.getId(), actualStock.getId());
        assertEquals(expectedStock.getSymbol(), actualStock.getSymbol());
        assertEquals(expectedStock.getCompanyName(), actualStock.getCompanyName());
        assertEquals(expectedStock.getPrice(), actualStock.getPrice());
    }

    public static void assertResponseStockDTOEqualsStock(final ResponseStockDTO expected, final ResponseStockDTO actual) {
        assertEquals(expected.id(), actual.id());
        assertEquals(expected.symbol(), actual.symbol());
        assertEquals(expected.companyName(), actual.companyName());
        assertEquals(expected.price(), actual.price());
    }
}
