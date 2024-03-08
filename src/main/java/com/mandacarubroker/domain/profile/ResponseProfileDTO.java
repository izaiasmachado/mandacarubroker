package com.mandacarubroker.domain.profile;

import com.mandacarubroker.domain.user.User;

import java.time.LocalDate;

public record ResponseProfileDTO(
        String email,
        String username,
        String firstName,
        String lastName,
        LocalDate birthDate,
        Double balance
) {
    public static ResponseProfileDTO fromUser(final User user) {
        return new ResponseProfileDTO(
                user.getEmail(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getBalance()
        );
    }
}
