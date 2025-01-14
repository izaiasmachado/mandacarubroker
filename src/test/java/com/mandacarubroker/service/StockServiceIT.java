package com.mandacarubroker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
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

import static com.mandacarubroker.domain.stock.StockUtils.assertResponseStockDTOEqualsStock;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class StockServiceIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockService service;

    private ResponseStockDTO stock;
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
        stockId = stock.id();
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
        List<ResponseStockDTO> stocks = service.getAllStocks();
        assertEquals(stockSize, stocks.size());
    }

    @Test
    void itShouldBeAbleToGetStockById() {
        Optional<ResponseStockDTO> foundStock = service.getStockById(stockId);
        assertTrue(foundStock.isPresent());
        assertResponseStockDTOEqualsStock(stock, foundStock.get());
    }

    @Test
    void itShouldRetunEmptyWhenStockIsNotFound() {
        Optional<ResponseStockDTO> foundStock = service.getStockById(invalidStockId);
        assertEquals(Optional.empty(), foundStock);
    }

    @Test
    void itShouldBeAbleToCreateStock() {
        RequestStockDTO requestStockDTO = new RequestStockDTO(validSymbol, validCompanyName, validPrice);
        Optional<ResponseStockDTO> createdStock = service.createStock(requestStockDTO);

        assertStockWasCreated();
        assertTrue(createdStock.isPresent());
        assertEquals(validSymbol, createdStock.get().symbol());
        assertEquals(validCompanyName, createdStock.get().companyName());
        assertEquals(validPrice, createdStock.get().price());
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
        Optional<ResponseStockDTO> updatedStock = service.updateStock(stockId, requestStockDTO);

        assertNoStockWasCreated();
        assertTrue(updatedStock.isPresent());
        assertEquals(validSymbol, updatedStock.get().symbol());
        assertEquals(validCompanyName, updatedStock.get().companyName());
        assertEquals(validPrice, updatedStock.get().price());
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
        Optional<ResponseStockDTO> deletedStock = service.getStockById(stockId);
        assertEquals(Optional.empty(), deletedStock);
        assertStockWasDeleted();
    }

    @Test
    void itShouldNotBeAbleToDeleteStockThatDoesNotExists() {
        service.deleteStock(invalidStockId);
        assertNoStockWasCreated();
    }
}
