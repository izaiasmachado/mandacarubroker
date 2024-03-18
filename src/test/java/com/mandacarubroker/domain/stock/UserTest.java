package com.mandacarubroker.domain.stock;

import com.mandacarubroker.domain.user.RequestUserDTO;
import com.mandacarubroker.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mandacarubroker.exceptions.IllegalArgumentException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    private User user;
    private final RequestUserDTO requestUserDTO = new RequestUserDTO(
            "marcosloiola@yahoo.com",
            "Marcos23",
            "passmarco123",
            "Marcos",
            "Loiola",
            LocalDate.of(2002, 2, 26),
            1000.0
    );

    @BeforeEach
    void setUp() {
        user = new User(requestUserDTO);
    }

    @Test
    void itShouldBeAbleToDepositMoney() {
        double initialBalance = user.getBalance();
        double depositAmount = 100;
        double expectedBalance = initialBalance + depositAmount;

        user.deposit(depositAmount);
        assertEquals(expectedBalance, user.getBalance());
    }

    @Test
    void itShouldNotBeAbleToDepositNegativeAmount() {
        double depositAmount = -100;

        assertThrows(IllegalArgumentException.class, () -> {
            user.deposit(depositAmount);
        });
    }

    @Test
    void itShouldNotBeAbleToDepositZeroAmount() {
        double depositAmount = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            user.deposit(depositAmount);
        });
    }

    @Test
    void itShouldBeAbleToWithdrawMoney() {
        double initialBalance = user.getBalance();
        double withdrawAmount = 100;
        double expectedBalance = initialBalance - withdrawAmount;

        user.withdraw(withdrawAmount);
        assertEquals(expectedBalance, user.getBalance());
    }

    @Test
    void itShouldNotBeAbleToWithdrawNegativeAmount() {
        double withdrawAmount = -100;

        assertThrows(IllegalArgumentException.class, () -> {
            user.withdraw(withdrawAmount);
        });
    }

    @Test
    void itShouldNotBeAbleToWithdrawZeroAmount() {
        double withdrawAmount = 0;

        assertThrows(IllegalArgumentException.class, () -> {
            user.withdraw(withdrawAmount);
        });
    }

    @Test
    void itShouldNotBeAbleToWithdrawMoreThanBalance() {
        double withdrawAmount = 10000;

        assertThrows(IllegalArgumentException.class, () -> {
            user.withdraw(withdrawAmount);
        });
    }
}
