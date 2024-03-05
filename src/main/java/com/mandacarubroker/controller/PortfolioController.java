package com.mandacarubroker.controller;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/{stockId}/buy")
    public ResponseEntity<ResponseUserDTO> buyStock(
            @PathVariable final String stockId,
            @RequestBody @Valid final RequestStockOwnershipDTO shares) {
        ResponseUserDTO user = portfolioService.buyStock(stockId, shares);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{stockId}/sell")
    public ResponseEntity<ResponseUserDTO> sellStock(
            @PathVariable final String stockId,
            @RequestBody @Valid final RequestStockOwnershipDTO shares) {
        ResponseUserDTO user = portfolioService.sellStock(stockId, shares);
        return ResponseEntity.ok(user);
    }
}
