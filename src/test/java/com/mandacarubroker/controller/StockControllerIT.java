package com.mandacarubroker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.service.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class StockControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StockService service;

    private final String urlRequestInvalidStock = "/stocks/dummy-stock-id";
    private final String validSymbol = "MDDC2";
    private final String validCompanyName = "Mandacaru Inc.";
    private final double validPrice = 0.01;
    private final RequestStockDTO validStockDTO = new RequestStockDTO(validSymbol, validCompanyName, validPrice);
    private final RequestStockDTO invalidSymbolStockDTO = new RequestStockDTO("MDDAAA2", validCompanyName, validPrice);
    private final String invalidPriceStockJsonString = "{\"symbol\":\"MDDC2\",\"companyName\":\"Mandacaru Inc.\",\"price\":\"40A\"}";
    private final String stockChangedId = "{\"id\":\"new-id\",\"symbol\":\"MDDC2\",\"companyName\":\"Mandacaru Inc.\",\"price\":\"1.00\"}";

    private ResponseStockDTO stock;
    private String stockId;
    private String urlRequestStockById;

    @BeforeEach
    void setUp() {
        List<ResponseStockDTO> stocks = service.getAllStocks();
        stock = stocks.get(0);
        stockId = stock.id();
        urlRequestStockById = "/stocks/" + stockId;
    }

    @AfterEach
    void tearDown() {
    }

    void assertRequestDTOEqualsStock(final RequestStockDTO stockDTO, final Stock receivedStock) {
        assertEquals(stockDTO.symbol(), receivedStock.getSymbol());
        assertEquals(stockDTO.companyName(), receivedStock.getCompanyName());
        assertEquals(stockDTO.price(), receivedStock.getPrice());
    }

    @Test
    void itShouldReturnOkStatusWhenGetAllStocks() throws Exception {
        RequestBuilder requestBuilder = get("/stocks");
        ResultMatcher resultMatcher = status().isOk();
        mockMvc.perform(requestBuilder).andExpect(resultMatcher);
    }

    @Test
    void itShouldBeAbleToGetAllStocks() throws Exception {
        RequestBuilder requestBuilder = get("/stocks");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        List<Stock> stocks = objectMapper.readValue(content, List.class);

        assertEquals(service.getAllStocks().size(), stocks.size());
    }

    @Test
    void itShouldBeAbleToGetStockById() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(stock);

        RequestBuilder requestBuilder = get(urlRequestStockById);
        ResultMatcher matchResponse = content().json(stockJsonString);

        mockMvc.perform(requestBuilder).andExpectAll(matchResponse);
    }

    @Test
    void itShouldReturnOkStatusWhenGetStockById() throws Exception {
        RequestBuilder requestBuilder = get(urlRequestStockById);
        ResultMatcher matchStatus = status().isOk();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNotFoundWhenGetStockDoesNotExists() throws Exception {
        RequestBuilder requestBuilder = get(urlRequestInvalidStock);
        ResultMatcher matchStatus = status().isNotFound();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnCreatedStatusAfterSucessfulPost() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(validStockDTO);

        RequestBuilder requestBuilder = post("/stocks")
                .contentType("application/json")
                .content(stockJsonString);
        ResultMatcher matchStatus = status().isCreated();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnStockDataAfterSucessfulPost() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(validStockDTO);

        RequestBuilder requestBuilder = post("/stocks")
                .contentType("application/json")
                .content(stockJsonString);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        Stock createdStock = objectMapper.readValue(content, Stock.class);

        assertRequestDTOEqualsStock(validStockDTO, createdStock);
    }

    @Test
    void itShouldHandlePostInvalidStockSymbol() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(invalidSymbolStockDTO);

        RequestBuilder requestBuilder = post("/stocks")
                .contentType("application/json")
                .content(stockJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePostInvalidStockPrice() throws Exception {
        RequestBuilder requestBuilder = post("/stocks")
                .contentType("application/json")
                .content(invalidPriceStockJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnStockDataAfterSucessfulPut() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(validStockDTO);

        RequestBuilder requestBuilder = put(urlRequestStockById)
                .contentType("application/json")
                .content(stockJsonString);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        Stock updatedStock = objectMapper.readValue(content, Stock.class);

        assertRequestDTOEqualsStock(validStockDTO, updatedStock);
    }

    @Test
    void itShouldReturnOkStatusAfterSucessfulPut() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(validStockDTO);

        RequestBuilder requestBuilder = put(urlRequestStockById)
                .contentType("application/json")
                .content(stockJsonString);
        ResultMatcher matchStatus = status().isOk();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNotFoundWhenPutStockDoesNotExists() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(validStockDTO);

        RequestBuilder requestBuilder = put(urlRequestInvalidStock)
                .contentType("application/json")
                .content(stockJsonString);
        ResultMatcher matchStatus = status().isNotFound();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePutInvalidStockSymbol() throws Exception {
        String stockJsonString = objectMapper.writeValueAsString(invalidSymbolStockDTO);

        RequestBuilder requestBuilder = put(urlRequestStockById)
                .contentType("application/json")
                .content(stockJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldHandlePutInvalidStockPrice() throws Exception {
        RequestBuilder requestBuilder = put(urlRequestStockById)
                .contentType("application/json")
                .content(invalidPriceStockJsonString);
        ResultMatcher matchStatus = status().isBadRequest();

        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldNotBeAbleToPutStockId() throws Exception {
        RequestBuilder requestBuilder = put(urlRequestStockById)
                .contentType("application/json")
                .content(stockChangedId);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String content = result.getResponse().getContentAsString();
        System.out.println(content);
        ResponseStockDTO createdStock = objectMapper.readValue(content, ResponseStockDTO.class);

        assertEquals(stockId, createdStock.id());
    }

    @Test
    void itShouldBeAbleToDeleteStock() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestStockById);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
        assertEquals(Optional.empty(), service.getStockById(stockId));
    }

    @Test
    void itShouldReturnNoContentStatusWhenDeleteStock() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestStockById);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }

    @Test
    void itShouldReturnNoContentStatusWhenDeleteStockDoesNotExists() throws Exception {
        RequestBuilder requestBuilder = delete(urlRequestInvalidStock);
        ResultMatcher matchStatus = status().isNoContent();
        mockMvc.perform(requestBuilder).andExpect(matchStatus);
    }
}
