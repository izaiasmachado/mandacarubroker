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
}

