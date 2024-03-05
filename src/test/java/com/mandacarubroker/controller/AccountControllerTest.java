package com.mandacarubroker.controller;

import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountControllerTest {
    @MockBean
    private AccountService accountService;
    private AccountController accountController;

    private static final double USER_BALANCE = 100.00;
    private static final double POSITIVE_DEPOSIT_AMOUNT = 50.00;

    private RequestUserDTO requestUserDTO = new RequestUserDTO(
            "email@example.com",
            "username",
            "passwordddd123",
            "first-name",
            "last-name",
            LocalDate.of(1997, 4, 5),
            USER_BALANCE
    );
    private User user = new User(requestUserDTO);
    private ResponseUserDTO responseUserDTO = ResponseUserDTO.fromUser(user);

    @BeforeEach
    void setUp() {
        accountService = Mockito.mock(AccountService.class);
        accountController = new AccountController(accountService);
    }

    @Test
    void itShouldBeAbleToDepositMoney() {
        Mockito.when(accountService.doDepositForAuthenticatedUser(POSITIVE_DEPOSIT_AMOUNT)).thenReturn(java.util.Optional.of(responseUserDTO));
        ResponseUserDTO response = accountController.deposit(POSITIVE_DEPOSIT_AMOUNT).getBody();

        assertEquals(responseUserDTO.id(), response.id());
        assertEquals(responseUserDTO.email(), response.email());
        assertEquals(responseUserDTO.username(), response.username());
        assertEquals(responseUserDTO.firstName(), response.firstName());
        assertEquals(responseUserDTO.lastName(), response.lastName());
        assertEquals(responseUserDTO.birthDate(), response.birthDate());
        assertEquals(responseUserDTO.balance(), response.balance());
    }

    @Test
    void itShouldNotBeAbleToDepositNegativeAmount() {
        Mockito.when(accountService.doDepositForAuthenticatedUser(-POSITIVE_DEPOSIT_AMOUNT)).thenReturn(java.util.Optional.empty());

        int statusCode = accountController.deposit(-POSITIVE_DEPOSIT_AMOUNT).getStatusCodeValue();
        assertEquals(statusCode, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void itShouldNotBeAbleToDepositZeroAmount() {
        Mockito.when(accountService.doDepositForAuthenticatedUser(0.00)).thenReturn(java.util.Optional.empty());

        int statusCode = accountController.deposit(0.00).getStatusCodeValue();
        assertEquals(statusCode, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void itShouldBeAbleToWithdrawMoney() {
        Mockito.when(accountService.doWithdrawForAuthenticatedUser(POSITIVE_DEPOSIT_AMOUNT)).thenReturn(java.util.Optional.of(responseUserDTO));
        ResponseUserDTO response = accountController.withdraw(POSITIVE_DEPOSIT_AMOUNT).getBody();

        assertEquals(responseUserDTO.id(), response.id());
        assertEquals(responseUserDTO.email(), response.email());
        assertEquals(responseUserDTO.username(), response.username());
        assertEquals(responseUserDTO.firstName(), response.firstName());
        assertEquals(responseUserDTO.lastName(), response.lastName());
        assertEquals(responseUserDTO.birthDate(), response.birthDate());
        assertEquals(responseUserDTO.balance(), response.balance());
    }

    @Test
    void itShouldNotBeAbleToWithdrawNegativeAmount() {
        Mockito.when(accountService.doWithdrawForAuthenticatedUser(-POSITIVE_DEPOSIT_AMOUNT)).thenReturn(java.util.Optional.empty());

        int statusCode = accountController.withdraw(-POSITIVE_DEPOSIT_AMOUNT).getStatusCodeValue();
        assertEquals(statusCode, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void itShouldNotBeAbleToWithdrawZeroAmount() {
        Mockito.when(accountService.doWithdrawForAuthenticatedUser(0.00)).thenReturn(java.util.Optional.empty());

        int statusCode = accountController.withdraw(0.00).getStatusCodeValue();
        assertEquals(statusCode, HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void itShouldNotBeAbleToWithdrawMoreThanBalance() {
        Mockito.when(accountService.doWithdrawForAuthenticatedUser(USER_BALANCE + 1)).thenReturn(java.util.Optional.empty());

        int statusCode = accountController.withdraw(USER_BALANCE + 1).getStatusCodeValue();
        assertEquals(statusCode, HttpStatus.BAD_REQUEST.value());
    }
}