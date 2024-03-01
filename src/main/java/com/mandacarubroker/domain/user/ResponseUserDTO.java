package com.mandacarubroker.domain.user;

import java.time.LocalDate;

public record ResponseUserDTO(
        String firstName,
        String lastName,
        LocalDate birthDate,
        double balance
) {
}

