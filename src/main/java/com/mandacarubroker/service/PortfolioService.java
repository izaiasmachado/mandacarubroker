package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    private final StockOwnershipRepository stockPositionRepository;

    public PortfolioService(final StockOwnershipRepository receivedStockPositionRepository) {
        this.stockPositionRepository = receivedStockPositionRepository;
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
}
