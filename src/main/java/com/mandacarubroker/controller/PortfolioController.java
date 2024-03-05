package com.mandacarubroker.controller;

import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.service.PortfolioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(final PortfolioService receivedPortfolioService) {
        this.portfolioService = receivedPortfolioService;
    }

    @GetMapping
    public List<ResponseStockOwnershipDTO> getAuthenticatedUserStockPortfolio() {
        return portfolioService.getAuthenticatedUserStockPortfolio();
    }

    @GetMapping("/stock/{stockId}")
    public ResponseStockOwnershipDTO getStockPositionById(@PathVariable final String stockId) {
        Optional<ResponseStockOwnershipDTO> stockPosition = portfolioService.getAuthenticatedUserStockOwnershipByStockId(stockId);

        if (stockPosition.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock ownership not found");
        }

        return stockPosition.get();
    }
}
