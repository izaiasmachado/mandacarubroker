package com.mandacarubroker.controller;

import com.mandacarubroker.domain.position.RequestStockOwnershipDTO;
import com.mandacarubroker.domain.position.ResponseStockOwnershipDTO;
import com.mandacarubroker.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Portfólio de Ativos", description = "Operações relacionadas com portfólios de ativos. User role: user")
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(final PortfolioService receivedPortfolioService) {
        this.portfolioService = receivedPortfolioService;
    }

    @Operation(summary = "Listagem do portfólio de ativos do usuário", description = "Listagem do portfólio de ativos do usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Portfólio encontrado")
    })
    @GetMapping
    public List<ResponseStockOwnershipDTO> getAuthenticatedUserStockPortfolio() {
        return portfolioService.getAuthenticatedUserStockPortfolio();
    }

    @Operation(summary = "Comprar ações", description = "Compra um ou vários títulos de uma ações")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação de compra efetuada")
    })
    @PostMapping("/stock/{stockId}/buy")
    public ResponseEntity<ResponseStockOwnershipDTO> buyStock(
            @PathVariable final String stockId,
            @RequestBody @Valid final RequestStockOwnershipDTO shares
    ) {
        ResponseStockOwnershipDTO stockPosition = portfolioService.buyStock(stockId, shares);
        return ResponseEntity.ok(stockPosition);
    }

    @Operation(summary = "Vender ações", description = "Vende um ou vários títulos de uma ação no portfolio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação de venda efetuada")
    })
    @PostMapping("/stock/{stockId}/sell")
    public ResponseEntity<ResponseStockOwnershipDTO> sellStock(
            @PathVariable final String stockId,
            @RequestBody @Valid final RequestStockOwnershipDTO shares
    ) {
        ResponseStockOwnershipDTO stockPosition = portfolioService.sellStock(stockId, shares);
        return ResponseEntity.ok(stockPosition);
    }

    @Operation(summary = "Detalhes sobre a posse de ações de uma empresa", description = "Mostra detalhes sobre a posse de ações de uma empresa por um usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ação encontrada"),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada")
    })
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<ResponseStockOwnershipDTO> getStockPositionById(@PathVariable final String stockId) {
        ResponseStockOwnershipDTO stockPosition = portfolioService.getAuthenticatedUserStockOwnershipByStockId(stockId);
        return ResponseEntity.ok(stockPosition);
    }
}
