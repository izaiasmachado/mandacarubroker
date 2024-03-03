package com.mandacarubroker.controller;

import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.service.AccountService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;

    public AccountController(final AccountService receivedAccountService) {
        this.accountService = receivedAccountService;
    }

    @GetMapping("/deposit")
    public ResponseEntity<ResponseUserDTO> deposit(@RequestParam @Positive final double amount) {
        ResponseUserDTO user = accountService.doDepositForAuthenticatedUser(amount);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/withdraw")
    public ResponseEntity<ResponseUserDTO> withdraw(@RequestParam @Positive final double amount) {
        ResponseUserDTO user = accountService.doWithdrawForAuthenticatedUser(amount);
        return ResponseEntity.ok(user);
    }
}
