package com.mandacarubroker.controller;

import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Tag(name = "Account", description = "Operações de conta do usuário. User role: user")
@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;

    public AccountController(final AccountService receivedAccountService) {
        this.accountService = receivedAccountService;
    }

    @Operation(summary = "Retorna saldo atualizado", description = "Retorna um usuário com o saldo atualizado após o depósito")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo autalizado após o depósito"),
            @ApiResponse(responseCode = "400", description = "Erro nas verificações de saldo")
    })
    @GetMapping("/deposit")
    public ResponseEntity<ResponseUserDTO> deposit(@RequestParam @Positive final double amount) {
        Optional<ResponseUserDTO> user = accountService.doDepositForAuthenticatedUser(amount);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Retorna saldo atualizado", description = "Retorna um usuário com o saldo atualizado após o saque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo autalizado após o saque"),
            @ApiResponse(responseCode = "400", description = "Erro nas verificações de saldo")
    })
    @GetMapping("/withdraw")
    public ResponseEntity<ResponseUserDTO> withdraw(@RequestParam @Positive final double amount) {
        Optional<ResponseUserDTO> user = accountService.doWithdrawForAuthenticatedUser(amount);

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(user.get());
    }
}
