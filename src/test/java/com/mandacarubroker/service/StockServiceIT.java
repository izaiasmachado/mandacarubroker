package com.mandacarubroker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
class StockServiceIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockService service;

    private Stock stock;
    private double stockSize;
    private String stockId;
    private String invalidStockId = "dummy-stock-id";
    private final String validSymbol = "AAPL1";
    private final String emptySymbol = "";
    private final String invalidSymbol = "AAPL@";
    private final String validCompanyName = "Apple Inc.";
    private final String emptyCompanyName = "";
    private final double validPrice = 150.00;
    private final double negativePrice = -1.00;
    private final double zeroPrice = 0.00;

    @BeforeEach
    void setUp() {
        stockSize = service.getAllStocks().size();
        stock = service.getAllStocks().get(0);
        stockId = stock.getId();
    }

    @AfterEach
    void tearDown() {
    }

    void assertNoStockWasCreated() {
        assertEquals(stockSize, service.getAllStocks().size());
    }

    void assertStockWasCreated() {
        assertEquals(stockSize + 1, service.getAllStocks().size());
    }

    void assertStockWasDeleted() {
        assertEquals(stockSize - 1, service.getAllStocks().size());
    }

    void assertStocksAreEqual(final Stock expected, final Stock actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSymbol(), actual.getSymbol());
        assertEquals(expected.getCompanyName(), actual.getCompanyName());
        assertEquals(expected.getPrice(), actual.getPrice());
    }

    void assertRequestStockDTOEqualsStock(final RequestStockDTO expected, final Stock actual) {
        assertEquals(expected.symbol(), actual.getSymbol());
        assertEquals(expected.companyName(), actual.getCompanyName());
        assertEquals(expected.price(), actual.getPrice());
    }

    @Test
    void itShouldBeAbleToGetAllStocks() {
        List<Stock> stocks = service.getAllStocks();
        assertEquals(stockSize, stocks.size());
    }

    @Test
    void itShouldBeAbleToGetStockById() {
        Stock foundStock = service.getStockById(stockId).get();
        assertStocksAreEqual(stock, foundStock);
    }

    @Test
    void itShouldRetunEmptyWhenStockIsNotFound() {
        Optional<Stock> foundStock = service.getStockById(invalidStockId);
        assertEquals(Optional.empty(), foundStock);
    }

    @Test
    void itShouldBeAbleToCreateStock() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, validPrice);
        Stock createdStock = service.createStock(requestStockDTO);

        assertStockWasCreated();
        assertRequestStockDTOEqualsStock(requestStockDTO, createdStock);
    }

    @Test
    void itShouldNotBeAbleToCreateStockWithEmptySymbol() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(emptySymbol, validCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.createStock(requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToCreateStockWithInvalidSymbol() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(invalidSymbol, validCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.createStock(requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToCreateStockWithEmptyCompanyName() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, emptyCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.createStock(requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToCreateStockWithNegativePrice() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, negativePrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.createStock(requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToCreateStockWithZeroPrice() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, zeroPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.createStock(requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldBeAbleToUpdateStock() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, validPrice);
        Stock updatedStock = service.updateStock(stockId, requestStockDTO).get();

        assertRequestStockDTOEqualsStock(requestStockDTO, updatedStock);
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToUpdateStockWithEmptySymbol() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(emptySymbol, validCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateStock(stockId, requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToUpdateStockWithInvalidSymbol() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(invalidSymbol, validCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateStock(stockId, requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToUpdateStockWithEmptyCompanyName() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, emptyCompanyName, validPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateStock(stockId, requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToUpdateStockWithNegativePrice() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, negativePrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateStock(stockId, requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldNotBeAbleToUpdateStockWithZeroPrice() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, zeroPrice);
        assertThrows(ConstraintViolationException.class, () -> {
            service.updateStock(stockId, requestStockDTO);
        });
        assertNoStockWasCreated();
    }

    @Test
    void itShouldBeAbleToDeleteStock() {
        service.deleteStock(stockId);
        Optional<Stock> deletedStock = service.getStockById(stockId);

        assertEquals(Optional.empty(), deletedStock);
        assertStockWasDeleted();
    }

    @Test
    void itShouldNotBeAbleToDeleteStockThatDoesNotExists() {
        service.deleteStock(invalidStockId);
        assertNoStockWasCreated();
    }
}
