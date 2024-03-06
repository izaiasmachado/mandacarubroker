package com.mandacarubroker.controller;

import com.mandacarubroker.domain.stock.RequestStockDTO;
import com.mandacarubroker.domain.stock.ResponseStockDTO;
import com.mandacarubroker.service.StockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Tag(name = "Ação", description = "Operações de CRUD das ações")
@RestController
@RequestMapping("/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(final StockService receivedStockService) {
        this.stockService = receivedStockService;
    }

    @Operation(summary = "Retorna todas as ações", description = "Retorna uma lista com todas as ações cadastradas")
    @GetMapping
    public List<ResponseStockDTO> getAllStocks() {
        return stockService.getAllStocks();
    }

    @Operation(summary = "Retorna uma ação", description = "Retorna uma ação cadastrada com base no id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ação encontrada"),
        @ApiResponse(responseCode = "404", description = "Ação não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStockDTO> getStockById(@PathVariable final String id) {
        Optional<ResponseStockDTO> stock = stockService.getStockById(id);

        if (stock.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(stock.get());
    }

    @Operation(summary = "Cria uma ação", description = "Cria uma nova ação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ação criada"),
        @ApiResponse(responseCode = "400", description = "Dados da ação são inválidos")
    })
    @PostMapping
    public ResponseEntity<ResponseStockDTO> createStock(@Valid @RequestBody final RequestStockDTO requestStockDTO) {
        Optional<ResponseStockDTO> createdStock = stockService.createStock(requestStockDTO);

        if (createdStock.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdStock.get());
    }

    @Operation(summary = "Atualiza uma ação", description = "Atualiza uma ação cadastrada com base no id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ação atualizada"),
        @ApiResponse(responseCode = "404", description = "Ação não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados da ação são inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStockDTO> updateStock(@PathVariable final String id, @Valid @RequestBody final RequestStockDTO updatedStockDTO) {
        Optional<ResponseStockDTO> updatedStock = stockService.updateStock(id, updatedStockDTO);

        if (updatedStock.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedStock.get());
    }

    @Operation(summary = "Deleta uma ação", description = "Deleta uma ação cadastrada com base no id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteStock(@PathVariable final String id) {
        stockService.deleteStock(id);
        return ResponseEntity.noContent().build();
    }
}
