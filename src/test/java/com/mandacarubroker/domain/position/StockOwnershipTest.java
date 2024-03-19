package com.mandacarubroker.domain.position;

import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mandacarubroker.exceptions.IllegalArgumentException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StockOwnershipTest {
    private final RequestUserDTO requestUserDTO = new RequestUserDTO(
            "marcosloiola@yahoo.com",
            "Marcos22",
            "passmarco123",
            "Marcos",
            "Loiola",
            LocalDate.of(2002, 2, 26),
            0
    );
    private final User validUser = new User(requestUserDTO);
    private final RequestStockOwnershipDTO requestStockOwnershipDTO = new RequestStockOwnershipDTO(10);
    private final Stock validStock = new Stock("stock-id", "symbol", "companyName", 100.0);
    private StockOwnership stockOwnership;

    @BeforeEach
    void setUp() {
        stockOwnership = new StockOwnership(requestStockOwnershipDTO, validStock, validUser);
    }

    @Test
    void itShouldBeAbleToBuyStocks() {
        final int sharesToBuy = 10;
        final int expectedShares = stockOwnership.getShares() + sharesToBuy;

        stockOwnership.buyShares(sharesToBuy);
        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldNotBeAbleToBuyZeroShares() {
        final int sharesToBuy = 0;
        final int expectedShares = stockOwnership.getShares();
        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldNotBeAbleToBuyNegativeShares() {
        final int sharesToBuy = -10;
        final int expectedShares = stockOwnership.getShares();

        assertThrows(IllegalArgumentException.class, () -> stockOwnership.buyShares(sharesToBuy));

        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldBeAbleToSellStocks() {
        final int sharesToSell = 10;
        final int expectedShares = stockOwnership.getShares() - sharesToSell;

        stockOwnership.sellShares(sharesToSell);
        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldNotBeAbleToSellZeroShares() {
        final int sharesToSell = 0;
        final int expectedShares = stockOwnership.getShares();
        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldNotBeAbleToSellNegativeShares() {
        final int sharesToSell = -10;
        final int expectedShares = stockOwnership.getShares();

        assertThrows(IllegalArgumentException.class, () -> stockOwnership.sellShares(sharesToSell));

        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldNotBeAbleToSellMoreSharesThanOwned() {
        final int sharesToSell = 100;
        final int expectedShares = stockOwnership.getShares();

        assertThrows(IllegalArgumentException.class, () -> stockOwnership.sellShares(sharesToSell));

        assertEquals(expectedShares, stockOwnership.getShares());
    }

    @Test
    void itShouldBeAbleToGetTotalValue() {
        final double expectedTotalValue = stockOwnership.getShares() * validStock.getPrice();
        assertEquals(expectedTotalValue, stockOwnership.getTotalValue());
    }
}
