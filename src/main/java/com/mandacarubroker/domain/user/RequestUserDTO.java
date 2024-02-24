package com.mandacarubroker.domain.user;

import java.time.LocalDate;

public record RequestUserDTO(
        String email,
        String username,
        String password,
        String firstName,
        String lastName,
        LocalDate birthDate,
        double balance
) { }

