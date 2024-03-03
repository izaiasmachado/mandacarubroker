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
}
