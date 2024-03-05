package com.mandacarubroker.domain.user;

import java.time.LocalDate;

public record ResponseUserDTO(
        String id,
        String email,
        String username,
        String firstName,
        String lastName,
        LocalDate birthDate,
        double balance
) {
    public static ResponseUserDTO fromUser(final User user) {
        return new ResponseUserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getBalance()
        );
    }
}

