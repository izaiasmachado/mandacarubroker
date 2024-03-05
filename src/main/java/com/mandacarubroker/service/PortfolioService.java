package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.user.ResponseUserDTO;
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

    public PortfolioService(
            final StockOwnershipRepository receivedStockPositionRepository,
            final StockService receivedStockService,
            final UserRepository recievedUserRepository
            ) {
        this.stockPositionRepository = receivedStockPositionRepository;
        this.stockService = receivedStockService;
        this.userRepository = recievedUserRepository;
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
            final User user,
            final RequestStockOwnershipDTO shares
    ) {

        Optional<ResponseStockOwnershipDTO> existentStockPosition =
                getStockPositionByUserIdAndStockId(stock.getId());

        if (existentStockPosition.isPresent()) {
            Optional<StockOwnership> updatedStockPosition = updateShares(
                    existentStockPosition.get().id(),
                    new RequestStockOwnershipDTO(
                            existentStockPosition.get().totalShares()
                                    + shares.shares())
                    );

            if (updatedStockPosition.isEmpty()) {
                throw new IllegalStateException("Error on update shares in portfolio");
            }
            return ResponseStockOwnershipDTO.fromStockPosition(
                    updatedStockPosition.get()
            );
        }

        return  ResponseStockOwnershipDTO.fromStockPosition(
                createStockPosition(
                    new RequestStockOwnershipDTO(shares.shares()),
                    stock,
                    user
                )
        );
    }

    public ResponseUserDTO buyStock(final String stockId, final RequestStockOwnershipDTO shares) {
        User user = AuthService.getAuthenticatedUser();
        Optional<Stock> stock = stockService.getStockById(stockId);

        if (stock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found");
        }
        double userBalance = user.getBalance();
        double stockBoughtPrice = stock.get().getPrice() * shares.shares();
        if (userBalance < stockBoughtPrice) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Insufficient balance");
        }

        addStockInPortfolio(stock.get(), user, shares);
        user.setBalance(userBalance - stockBoughtPrice);

        return ResponseUserDTO.fromUser(userRepository.save(user));
    }
}
