package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import com.mandacarubroker.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    private final StockOwnershipRepository stockOwnershipRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    public PortfolioService(final StockOwnershipRepository receivedStockPositionRepository, final StockRepository receivedStockRepository, final UserRepository recievedUserRepository) {
        this.stockOwnershipRepository = receivedStockPositionRepository;
        this.stockRepository = receivedStockRepository;
        this.userRepository = recievedUserRepository;
    }

    public ResponseStockOwnershipDTO getAuthenticatedUserStockOwnershipByStockId(final String stockId) {
        User user = AuthService.getAuthenticatedUser();
        return getStockOwnershipByStockId(user, stockId);
    }

    public ResponseStockOwnershipDTO getStockOwnershipByStockId(final User user, final String stockId) {
        Stock stock = getStock(stockId);
        StockOwnership stockOwnership = getStockOwnership(user, stock);
        return ResponseStockOwnershipDTO.fromStockOwnership(stockOwnership);
    }

    private Stock getStock(final String stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        if (stock.isEmpty()) {
            throw new NotFoundException("Stock not found");
        }

        return stock.get();
    }

    private StockOwnership getStockOwnership(
            final User user,
            final Stock stock
    ) {
        StockOwnership stockOwnership = stockOwnershipRepository.findByUserIdAndStockId(user.getId(), stock.getId());

        if (stockOwnership == null) {
            RequestStockOwnershipDTO requestStockOwnershipDTO = new RequestStockOwnershipDTO(0);
            return new StockOwnership(requestStockOwnershipDTO, stock, user);
        }

        return stockOwnership;
    }

    private StockOwnership updateStockOwnership(final StockOwnership stockOwnership) {
        if (stockOwnership.getShares() == 0) {
            stockOwnershipRepository.delete(stockOwnership);
            return stockOwnership;
        }

        return stockOwnershipRepository.save(stockOwnership);
    }

    public List<ResponseStockOwnershipDTO> getAuthenticatedUserStockPortfolio() {
        User user = AuthService.getAuthenticatedUser();
        String userId = user.getId();
        return getPortfolioByUserId(userId);
    }

    public List<ResponseStockOwnershipDTO> getPortfolioByUserId(final String userId) {
        List<StockOwnership> stockPositions = stockOwnershipRepository.findByUserId(userId);

        return stockPositions.stream()
                .map(ResponseStockOwnershipDTO::fromStockOwnership)
                .toList();
    }

    public ResponseStockOwnershipDTO buyStock(final String stockId, final RequestStockOwnershipDTO requestStockOwnershipDTO) {
        User user = AuthService.getAuthenticatedUser();
        Stock stock = getStock(stockId);

        int buyingShares = requestStockOwnershipDTO.shares();
        double buyingPrice = stock.getPrice() * buyingShares;
        user.withdraw(buyingPrice);
        userRepository.save(user);

        StockOwnership stockOwnership = getStockOwnership(user, stock);
        stockOwnership.buyShares(buyingShares);
        StockOwnership updatedStockOwnership = updateStockOwnership(stockOwnership);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockOwnership);
    }

    public ResponseStockOwnershipDTO sellStock(final String stockId, final RequestStockOwnershipDTO requestStockOwnershipDTO) {
        User user = AuthService.getAuthenticatedUser();
        Stock stock = getStock(stockId);

        int sellingShares = requestStockOwnershipDTO.shares();
        StockOwnership stockOwnership = getStockOwnership(user, stock);
        stockOwnership.sellShares(sellingShares);
        StockOwnership updatedStockOwnership = updateStockOwnership(stockOwnership);

        double sellingPrice = stock.getPrice() * sellingShares;
        user.deposit(sellingPrice);
        userRepository.save(user);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockOwnership);
    }
}
