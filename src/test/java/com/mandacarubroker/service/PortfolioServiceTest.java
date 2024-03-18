package com.mandacarubroker.service;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.position.StockOwnership;
import com.mandacarubroker.domain.position.StockOwnershipRepository;
import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.domain.stock.Stock;
import com.mandacarubroker.domain.stock.StockRepository;
import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import com.mandacarubroker.exceptions.NotFoundException;
import com.mandacarubroker.security.SecuritySecretsMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.mandacarubroker.domain.stock.StockUtils.assertResponseStockDTOEqualsStock;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class PortfolioServiceTest {
        @MockBean
        private StockOwnershipRepository stockOwnershipRepository;
        private UserRepository userRepository;

        @MockBean
        private StockRepository stockRepository;

        private PortfolioService portfolioService;

        private final RequestUserDTO validRequestUserDTO = new RequestUserDTO(
                "marcosloiola@yahoo.com",
                "Marcos22",
                "passmarco123",
                "Marcos",
                "Loiola",
                LocalDate.of(2002, 2, 26),
                0.25);

        private final User validUser = new User(validRequestUserDTO);

        private final RequestStockDTO requestAppleStockDTO = new RequestStockDTO("AAPL", "Apple Inc", 100.00);
        private final RequestStockDTO requestGoogleStockDTO = new RequestStockDTO("GOOGL", "Alphabet Inc", 2000.00);
        private final ResponseStockDTO responseAppleStockDTO = new ResponseStockDTO("apple-id", "AAPL", "Apple Inc",
                100.00);
        private final ResponseStockDTO responseGoogleStockDTO = new ResponseStockDTO("google-id", "GOOGL",
                "Alphabet Inc",
                2000.00);
        private final Stock appleStock = new Stock(requestAppleStockDTO);
        private final Stock googleStock = new Stock(requestGoogleStockDTO);

        private final StockOwnership appleStockOwnership = new StockOwnership(new RequestStockOwnershipDTO(100), appleStock, validUser);
        private final StockOwnership googleStockOwnership = new StockOwnership(new RequestStockOwnershipDTO(200), googleStock, validUser);

        private final List<ResponseStockOwnershipDTO> storedStockPortfolio = List.of(
                new ResponseStockOwnershipDTO(responseAppleStockDTO, 100, 10000.00),
                new ResponseStockOwnershipDTO(responseGoogleStockDTO, 200, 400000.00));
        private final List<StockOwnership> givenStockOwnerships = List.of(appleStockOwnership, googleStockOwnership);

        @BeforeEach
        void setUp() {
                SecuritySecretsMock.mockStatic();

                appleStock.setId("apple-id");
                googleStock.setId("google-id");

                stockOwnershipRepository = Mockito.mock(StockOwnershipRepository.class);
                stockRepository = Mockito.mock(StockRepository.class);
                Mockito.when(stockOwnershipRepository.findByUserId(validUser.getId())).thenReturn(givenStockOwnerships);

                userRepository = Mockito.mock(UserRepository.class);

                portfolioService = new PortfolioService(
                        stockOwnershipRepository,
                        stockRepository,
                        userRepository);
        }

        @Test
        void itShouldReturnTheStockPortfolioOfTheAuthenticatedUser() {
                mockStatic(AuthService.class);
                Mockito.when(AuthService.getAuthenticatedUser()).thenReturn(validUser);

                List<ResponseStockOwnershipDTO> stockPortfolio = portfolioService.getAuthenticatedUserStockPortfolio();

                for (int i = 0; i < stockPortfolio.size(); i++) {
                        assertResponseStockDTOEqualsStock(stockPortfolio.get(i).stock(),
                                this.storedStockPortfolio.get(i).stock());
                        assertEquals(stockPortfolio.get(i).totalShares(),
                                this.storedStockPortfolio.get(i).totalShares());
                        assertEquals(stockPortfolio.get(i).positionValue(),
                                this.storedStockPortfolio.get(i).positionValue());
                }
        }

        @Test
        void itShouldBeAbleToGetStockPortfolioByUserId() {
                List<ResponseStockOwnershipDTO> stockPortfolio = portfolioService
                        .getPortfolioByUserId(validUser.getId());

                for (int i = 0; i < stockPortfolio.size(); i++) {
                        assertResponseStockDTOEqualsStock(stockPortfolio.get(i).stock(),
                                this.storedStockPortfolio.get(i).stock());
                        assertEquals(stockPortfolio.get(i).totalShares(),
                                this.storedStockPortfolio.get(i).totalShares());
                        assertEquals(stockPortfolio.get(i).positionValue(),
                                this.storedStockPortfolio.get(i).positionValue());
                }
        }

        @Test
        void itShouldReturnUserStockOwnership() {
                Mockito.when(stockRepository.findById(appleStock.getId())).thenReturn(Optional.of(appleStock));
                Mockito.when(stockOwnershipRepository.findByUserIdAndStockId(validUser.getId(), appleStock.getId()))
                        .thenReturn(appleStockOwnership);

                ResponseStockOwnershipDTO stockOwnership = portfolioService.getStockOwnershipByStockId(validUser, appleStock.getId());

                assertResponseStockDTOEqualsStock(stockOwnership.stock(), responseAppleStockDTO);
                assertEquals(stockOwnership.totalShares(), appleStockOwnership.getShares());
                assertEquals(stockOwnership.positionValue(), appleStockOwnership.getTotalValue());
        }

        @Test
        void itShouldReturnUserStockOwnershipWithZeroShares() {
                Mockito.when(stockRepository.findById(googleStock.getId())).thenReturn(Optional.of(googleStock));

                ResponseStockOwnershipDTO stockOwnership = portfolioService.getStockOwnershipByStockId(validUser, googleStock.getId());

                assertResponseStockDTOEqualsStock(stockOwnership.stock(), responseGoogleStockDTO);
                assertEquals(stockOwnership.totalShares(), 0);
                assertEquals(stockOwnership.positionValue(), 0.00);
        }

        @Test
        void itShouldReturnEmptyWhenStockIsNotFound() {
                Mockito.when(stockRepository.findById("invalid-stock-id")).thenReturn(Optional.empty());

                assertThrows(NotFoundException.class, () -> {
                        portfolioService.getStockOwnershipByStockId(validUser, "invalid-stock-id");
                });
        }
}
