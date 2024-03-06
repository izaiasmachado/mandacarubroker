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
    private final StockOwnershipRepository stockPositionRepository;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    public PortfolioService(
            final StockOwnershipRepository receivedStockPositionRepository,
            final StockService receivedStockService,
            final StockRepository receivedStockRepository,
            final UserRepository recievedUserRepository) {
        this.stockPositionRepository = receivedStockPositionRepository;
        this.stockService = receivedStockService;
        this.stockRepository = receivedStockRepository;
        this.userRepository = recievedUserRepository;
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

        ResponseStockOwnershipDTO responseStockOwnershipDTO = ResponseStockOwnershipDTO
                .fromStockPosition(stockPosition);
        return Optional.of(responseStockOwnershipDTO);
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

    public Optional<ResponseStockOwnershipDTO> getStockPositionByUserIdAndStockId(final String stockId) {
        User user = AuthService.getAuthenticatedUser();
        String userId = user.getId();

        StockOwnership stockPosition = stockPositionRepository.findByUserIdAndStockId(userId, stockId);

        if (stockPosition == null) {
            return Optional.empty();
        }

        return Optional.of(ResponseStockOwnershipDTO.fromStockPosition(stockPosition));
    }

    private StockOwnership createStockPosition(
            final RequestStockOwnershipDTO requestStockOwnershipDTO,
            final ResponseStockDTO receivedStockDTO,
            final User user) {
        Stock stock = stockRepository.findById(receivedStockDTO.id()).orElse(null);
        StockOwnership newStockPosition = new StockOwnership(requestStockOwnershipDTO, stock, user);
        return stockPositionRepository.save(newStockPosition);
    }

    private Optional<StockOwnership> updateStockPositionShares(
            final String userId,
            final String stockId,
            final RequestStockOwnershipDTO requestStockOwnershipDTO) {

        StockOwnership stockPosition = stockPositionRepository.findByUserIdAndStockId(userId, stockId);
        stockPosition.setShares(requestStockOwnershipDTO.shares());
        return Optional.of(stockPositionRepository.save(stockPosition));
    }

    private ResponseStockOwnershipDTO addStockPositionInPortfolio(
            final ResponseStockDTO stock,
            final User user,
            final RequestStockOwnershipDTO shares) {

        Optional<ResponseStockOwnershipDTO> existentStockPosition = getStockPositionByUserIdAndStockId(stock.id());

        if (existentStockPosition.isPresent()) {
            Optional<StockOwnership> updatedStockPosition = updateStockPositionShares(
                    user.getId(),
                    stock.id(),
                    new RequestStockOwnershipDTO(
                            existentStockPosition.get().totalShares()
                                    + shares.shares()));

            if (updatedStockPosition.isEmpty()) {
                throw new IllegalStateException("Error on update shares in portfolio");
            }
            return ResponseStockOwnershipDTO.fromStockPosition(
                    updatedStockPosition.get());
        }

        return ResponseStockOwnershipDTO.fromStockPosition(
                createStockPosition(
                        new RequestStockOwnershipDTO(shares.shares()),
                        stock,
                        user));
    }

    private void removeStockPositionFromPortfolio(final String userId, final String stockId) {
        StockOwnership stockPosition = stockPositionRepository.findByUserIdAndStockId(userId, stockId);
        String stockPositionId = stockPosition.getId();
        stockPositionRepository.deleteById(stockPositionId);
    }

    public ResponseStockOwnershipDTO buyStock(final String stockId, final RequestStockOwnershipDTO shares) {
        User user = AuthService.getAuthenticatedUser();
        Optional<ResponseStockDTO> stock = stockService.getStockById(stockId);

        if (stock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        }
        double userBalance = user.getBalance();
        double stockBoughtPrice = stock.get().price() * shares.shares();
        if (userBalance < stockBoughtPrice) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient balance");
        }
        ResponseStockOwnershipDTO boughtStockPosition = addStockPositionInPortfolio(stock.get(), user, shares);
        user.setBalance(userBalance - stockBoughtPrice);
        userRepository.save(user);
        return boughtStockPosition;
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

        return ResponseStockOwnershipDTO.fromStockPosition(updatedStockPosition.get());
    }
}
