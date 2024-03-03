package com.mandacarubroker.service;

import com.mandacarubroker.domain.user.ResponseUserDTO;
import com.mandacarubroker.domain.user.User;
import com.mandacarubroker.domain.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private UserRepository userRepository;

    public AccountService(final UserRepository receivedUserRepository) {
        this.userRepository = receivedUserRepository;
    }

    public ResponseUserDTO doDepositForAuthenticatedUser(final double amount) {
        User user = AuthService.getAuthenticatedUser();
        return doDeposit(user, amount);
    }

    public ResponseUserDTO doDeposit(final User user, final double amount) {
        user.deposit(amount);
        User updatedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = ResponseUserDTO.fromUser(updatedUser);
        return responseUserDTO;
    }

    public ResponseUserDTO doWithdrawForAuthenticatedUser(final double amount) {
        User user = AuthService.getAuthenticatedUser();
        return doWithdraw(user, amount);
    }

    public ResponseUserDTO doWithdraw(final User user, final double amount) {
        user.withdraw(amount);
        User updatedUser = userRepository.save(user);
        ResponseUserDTO responseUserDTO = ResponseUserDTO.fromUser(updatedUser);
        return responseUserDTO;
    }
}
