package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Optional<ResponseStockOwnershipDTO> getAuthenticatedUserStockOwnershipByStockId(final String stockId) {
        User user = AuthService.getAuthenticatedUser();
        return getStockOwnershipByStockId(user, stockId);
    }

    public Optional<ResponseStockOwnershipDTO> getStockOwnershipByStockId(final User user, final String stockId) {
        Optional<Stock> stock = stockRepository.findById(stockId);

        if (stock.isEmpty()) {
            return Optional.empty();
        }

        StockOwnership stockOwnership = getStockOwnership(user, stock.get());
        ResponseStockOwnershipDTO responseStockOwnershipDTO = ResponseStockOwnershipDTO.fromStockOwnership(stockOwnership);
        return Optional.of(responseStockOwnershipDTO);
    }

    private StockOwnership getStockOwnership(
            final User user,
            final Stock stock
    ) {
        StockOwnership stockOwnership = stockOwnershipRepository.findByUserIdAndStockId(user.getId(), stock.getId());

        if (stockOwnership == null) {
            RequestStockOwnershipDTO requestStockOwnershipDTO = new RequestStockOwnershipDTO(0);
            StockOwnership newStockOwnership = new StockOwnership(requestStockOwnershipDTO, stock, user);
            return newStockOwnership;
        }

        return stockOwnership;
    }

    private StockOwnership updateStockOwnership(final StockOwnership stockOwnership) {
        if (stockOwnership.getShares() == 0) {
            stockOwnershipRepository.delete(stockOwnership);
            return stockOwnership;
        }

        StockOwnership updatedStockOwnership = stockOwnershipRepository.save(stockOwnership);
        return updatedStockOwnership;
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
        Optional<Stock> stock = stockRepository.findById(stockId);

        if (stock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        }

        int buyingShares = requestStockOwnershipDTO.shares();
        double buyingPrice = stock.get().getPrice() * buyingShares;
        user.withdraw(buyingPrice);
        userRepository.save(user);

        StockOwnership stockOwnership = getStockOwnership(user, stock.get());
        stockOwnership.buyShares(buyingShares);
        StockOwnership updatedStockOwnership = updateStockOwnership(stockOwnership);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockOwnership);
    }

    public ResponseStockOwnershipDTO sellStock(final String stockId, final RequestStockOwnershipDTO requestStockOwnershipDTO) {
        User user = AuthService.getAuthenticatedUser();
        Optional<Stock> stock = stockRepository.findById(stockId);

        if (stock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        }

        int sellingShares = requestStockOwnershipDTO.shares();
        StockOwnership stockOwnership = getStockOwnership(user, stock.get());
        stockOwnership.sellShares(sellingShares);
        StockOwnership updatedStockOwnership = updateStockOwnership(stockOwnership);

        double sellingPrice = stock.get().getPrice() * sellingShares;
        user.deposit(sellingPrice);
        userRepository.save(user);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockOwnership);
    }
}
