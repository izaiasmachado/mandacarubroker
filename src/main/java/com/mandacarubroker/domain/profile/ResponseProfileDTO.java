package com.mandacarubroker.domain.profile;

import java.time.LocalDate;

public record ResponseProfileDTO(
        String email,
        String username,
        String password,
        String firstName,
        String lastName,
        LocalDate birthDate
) {
}
