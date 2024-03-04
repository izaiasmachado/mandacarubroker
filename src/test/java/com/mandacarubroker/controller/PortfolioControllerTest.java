package com.mandacarubroker.controller;

import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.security.SecuritySecretsMock;
import com.mandacarubroker.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.mandacarubroker.domain.stock.StockUtils.assertResponseStockDTOEqualsStock;
import static com.mandacarubroker.domain.stock.StockUtils.assertStocksAreEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortfolioControllerTest {
    @MockBean
    private PortfolioService portfolioService;
    private final ResponseStockDTO responseAppleStockDTO = new ResponseStockDTO("id-apple","AAPL", "Apple Inc", 100.00);
    private final ResponseStockDTO responseGoogleStockDTO = new ResponseStockDTO("id-google", "GOOGL", "Alphabet Inc", 2000.00);

    private final List<ResponseStockOwnershipDTO> storedStockPortfolio = List.of(
            new ResponseStockOwnershipDTO("apple-stock-id", responseAppleStockDTO, 100, 10000.00),
            new ResponseStockOwnershipDTO("google-stock-id", responseGoogleStockDTO, 200, 400000.00)
    );

    @BeforeEach
    void setUp() {
        SecuritySecretsMock.mockStatic();

        portfolioService = Mockito.mock(PortfolioService.class);

        Mockito.when(portfolioService.getAuthenticatedUserStockPortfolio()).thenReturn(storedStockPortfolio);
    }

    @Test
    void itShouldReturnTheAuthenticatedUserStockPortfolio() {
        List<ResponseStockOwnershipDTO> expectedStockPortfolio = storedStockPortfolio;
        List<ResponseStockOwnershipDTO> actualStockPortfolio = portfolioService.getAuthenticatedUserStockPortfolio();

        assertEquals(expectedStockPortfolio.size(), actualStockPortfolio.size());

        for (int i = 0; i < expectedStockPortfolio.size(); i++) {
            ResponseStockOwnershipDTO expectedStockOwnership = expectedStockPortfolio.get(i);
            ResponseStockOwnershipDTO actualStockOwnership = actualStockPortfolio.get(i);
            ResponseStockDTO expectedStock = expectedStockOwnership.stock();
            ResponseStockDTO actualStock = actualStockOwnership.stock();

            assertEquals(expectedStockOwnership.id(), actualStockOwnership.id());
            assertEquals(expectedStockOwnership.totalShares(), actualStockOwnership.totalShares());
            assertEquals(expectedStockOwnership.positionValue(), actualStockOwnership.positionValue());
            assertResponseStockDTOEqualsStock(expectedStock, actualStock);
        }
    }
}
