package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
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
    private final StockService stockService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    public PortfolioService(
            final StockOwnershipRepository receivedStockPositionRepository,
            final StockService receivedStockService,
            final StockRepository receivedStockRepository,
            final UserRepository recievedUserRepository) {
        this.stockOwnershipRepository = receivedStockPositionRepository;
        this.stockService = receivedStockService;
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

    public Optional<ResponseStockOwnershipDTO> getStockPositionByUserIdAndStockId(final String stockId) {
        User user = AuthService.getAuthenticatedUser();
        String userId = user.getId();

        StockOwnership stockPosition = stockOwnershipRepository.findByUserIdAndStockId(userId, stockId);

        if (stockPosition == null) {
            return Optional.empty();
        }

        return Optional.of(ResponseStockOwnershipDTO.fromStockOwnership(stockPosition));
    }

    private Optional<StockOwnership> updateStockPositionShares(
            final String userId,
            final String stockId,
            final RequestStockOwnershipDTO requestStockOwnershipDTO) {

        StockOwnership stockPosition = stockOwnershipRepository.findByUserIdAndStockId(userId, stockId);
        stockPosition.setShares(requestStockOwnershipDTO.shares());
        return Optional.of(stockOwnershipRepository.save(stockPosition));
    }

    private void removeStockPositionFromPortfolio(final String userId, final String stockId) {
        StockOwnership stockPosition = stockOwnershipRepository.findByUserIdAndStockId(userId, stockId);
        String stockPositionId = stockPosition.getId();
        stockOwnershipRepository.deleteById(stockPositionId);
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
        StockOwnership updatedStockOwnership = stockOwnershipRepository.save(stockOwnership);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockOwnership);
    }

    public ResponseStockOwnershipDTO sellStock(final String stockId, final RequestStockOwnershipDTO shares) {
        User user = AuthService.getAuthenticatedUser();
        Optional<ResponseStockDTO> stock = stockService.getStockById(stockId);
        Optional<ResponseStockOwnershipDTO> userStockPosition = getStockPositionByUserIdAndStockId(stockId);

        if (userStockPosition.isEmpty() || stock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        }
        int sharesToSell = shares.shares();
        int userShares = userStockPosition.get().totalShares();
        if (sharesToSell > userShares) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "User with insufficient shares to sell");
        }
        double userBalance = user.getBalance();
        double stocksSoldPrice = stock.get().price() * sharesToSell;
        int remainingShares = userShares - sharesToSell;

        Optional<StockOwnership> updatedStockPosition = updateStockPositionShares(
                user.getId(),
                stockId,
                new RequestStockOwnershipDTO(
                        remainingShares));

        if (updatedStockPosition.isEmpty()) {
            throw new IllegalStateException("Error on update shares in portfolio");
        }

        if (remainingShares == 0) {
            removeStockPositionFromPortfolio(user.getId(), stockId);
        }
        user.setBalance(userBalance + stocksSoldPrice);
        userRepository.save(user);

        return ResponseStockOwnershipDTO.fromStockOwnership(updatedStockPosition.get());
    }
}
