package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;
import com.mandacarubroker.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    private final StockOwnershipRepository stockPositionRepository;
    private final StockRepository stockRepository;


    public PortfolioService(final StockOwnershipRepository receivedStockPositionRepository, final StockRepository receivedStockRepository) {
        this.stockPositionRepository = receivedStockPositionRepository;
        this.stockRepository = receivedStockRepository;
    }

    public List<ResponseStockOwnershipDTO> getAuthenticatedUserStockPortfolio() {
        User user = AuthService.getAuthenticatedUser();
        String userId = user.getId();
        return getPortfolioByUserId(userId);
    }

    public List<ResponseStockOwnershipDTO> getPortfolioByUserId(final String userId) {
        List<StockOwnership> stockPositions = stockPositionRepository.findByUserId(userId);

        return stockPositions.stream()
            .map(ResponseStockOwnershipDTO::fromStockPosition)
            .toList();
    }

    public Optional<ResponseStockOwnershipDTO> getAuthenticatedUserStockOwnershipByStockId(final String stockId) {
        User user = AuthService.getAuthenticatedUser();
        return getStockOwnershipByStockId(user, stockId);
    }

    public Optional<ResponseStockOwnershipDTO> getStockOwnershipByStockId(final User user, final String stockId) {
        String userId = user.getId();
        Stock stock = stockRepository.findById(stockId).orElse(null);

        if (stock == null) {
            return Optional.empty();
        }

        StockOwnership stockPosition = stockPositionRepository.findByUserIdAndStockId(userId, stockId);

        if (stockPosition == null) {
            return Optional.of(ResponseStockOwnershipDTO.fromStock(stock));
        }

        ResponseStockOwnershipDTO responseStockOwnershipDTO = ResponseStockOwnershipDTO.fromStockPosition(stockPosition);
        return Optional.of(responseStockOwnershipDTO);
    }
}
