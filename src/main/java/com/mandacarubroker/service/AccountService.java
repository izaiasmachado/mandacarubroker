package com.mandacarubroker.service;

import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private UserRepository userRepository;

    public AccountService(final UserRepository receivedUserRepository) {
        this.userRepository = receivedUserRepository;
    }

    public Optional<ResponseUserDTO> doDepositForAuthenticatedUser(final double amount) {
        User user = AuthService.getAuthenticatedUser();
        return doDeposit(user, amount);
    }

    public Optional<ResponseUserDTO> doDeposit(final User user, final double amount) {
        if (amount <= 0) {
            return Optional.empty();
        }

        user.deposit(amount);
        User updatedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = ResponseUserDTO.fromUser(updatedUser);
        return Optional.of(responseUserDTO);
    }

    public Optional<ResponseUserDTO> doWithdrawForAuthenticatedUser(final double amount) {
        User user = AuthService.getAuthenticatedUser();
        return doWithdraw(user, amount);
    }

    public Optional<ResponseUserDTO> doWithdraw(final User user, final double amount) {
        if (user.getBalance() < amount || amount <= 0) {
            return Optional.empty();
        }

        user.withdraw(amount);
        User updatedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = ResponseUserDTO.fromUser(updatedUser);
        return Optional.of(responseUserDTO);
    }
}
