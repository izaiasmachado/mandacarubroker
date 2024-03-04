package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public StockOwnership createStockPosition(
            final RequestStockOwnershipDTO requestStockOwnershipDTO,
            final Stock stock,
            final User user) {
        StockOwnership newStockPosition = new StockOwnership(requestStockOwnershipDTO, stock, user);
        return stockPositionRepository.save(newStockPosition);
    }

    public Optional<StockOwnership> updateShares(
            final String stockOwnershipId,
            final RequestStockOwnershipDTO requestStockOwnershipDTO
    ) {
        return stockPositionRepository.findById(stockOwnershipId)
                .map(stockOwnership -> {
                    stockOwnership.setShares(requestStockOwnershipDTO.shares());
                    return stockPositionRepository.save(stockOwnership);
                });
    }


    public ResponseStockOwnershipDTO addStockInPortfolio(
            final Stock stock,
            final User user
    ) {

        List<ResponseStockOwnershipDTO> userPortfolio = getPortfolioByUserId(user.getId());

        for (ResponseStockOwnershipDTO stockPosition : userPortfolio) {
            if (stockPosition.stock().getId().equals(stock.getId())) {
                Optional<StockOwnership> updatedStockPosition = updateShares(
                        stockPosition.id(),
                        new RequestStockOwnershipDTO(stockPosition.totalShares() + 1)
                );
                if (updatedStockPosition.isEmpty()) {
                    throw new IllegalStateException("Error on update shares in portfolio");
                }
                return ResponseStockOwnershipDTO.fromStockPosition(
                        updatedStockPosition.get()
                );
            }
        }

        return  ResponseStockOwnershipDTO.fromStockPosition(
                createStockPosition(
                    new RequestStockOwnershipDTO(1),
                    stock,
                    user
                )
        );
    }

}
