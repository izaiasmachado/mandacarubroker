package com.mandacarubroker.service;

import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mandacarubroker.validation.RecordValidation.validateRequestDTO;

@Service
public final class StockService {
    private final StockRepository stockRepository;

    public StockService(final StockRepository recievedStockRepository) {
        this.stockRepository = recievedStockRepository;
    }

    public List<ResponseStockDTO> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(ResponseStockDTO::fromStock)
                .toList();
    }

    public Optional<ResponseStockDTO> getStockById(final String stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        if (stock.isEmpty()) {
            return Optional.empty();
        }

        ResponseStockDTO responseStockDTO = ResponseStockDTO.fromStock(stock.get());
        return Optional.of(responseStockDTO);
    }

    public Optional<ResponseStockDTO> createStock(final RequestStockDTO requestStockDTO) {
        validateRequestDTO(requestStockDTO);
        Stock newStock = new Stock(requestStockDTO);
        Stock savedStock = stockRepository.save(newStock);
        ResponseStockDTO responseStockDTO = ResponseStockDTO.fromStock(savedStock);
        return Optional.of(responseStockDTO);
    }

    public Optional<ResponseStockDTO> updateStock(final String stockId, final RequestStockDTO requestStockDTO) {
        validateRequestDTO(requestStockDTO);

        Optional<Stock> foundStock = stockRepository.findById(stockId);

        if (foundStock.isEmpty()) {
            return Optional.empty();
        }

        Stock stock = foundStock.get();
        stock.setSymbol(requestStockDTO.symbol());
        stock.setCompanyName(requestStockDTO.companyName());
        stock.setPrice(requestStockDTO.price());

        Stock updatedStock = stockRepository.save(stock);
        ResponseStockDTO responseStockDTO = ResponseStockDTO.fromStock(updatedStock);
        return Optional.of(responseStockDTO);
    }

    public void deleteStock(final String id) {
        stockRepository.deleteById(id);
    }
}
