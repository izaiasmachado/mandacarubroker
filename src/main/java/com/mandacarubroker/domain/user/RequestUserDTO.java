package com.mandacarubroker.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RequestUserDTO(
        @Email(message = "The email format is invalid")
        String email,
        @NotBlank(message = "Username cannot be blank")
        String username,
        @Size(min = MINIMUM_PASSWORD_LENGTH, message = "Password must be at least 8 characters long")
        String password,
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        @MinimumAge(value = MINIMUM_AGE)
        LocalDate birthDate,
        @NotNull(message = "Balance cannot be null")
        @PositiveOrZero(message = "Balance cannot be negative")
        double balance
) {
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MINIMUM_AGE = 18;
}

