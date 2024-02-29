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
        @Size(min = MINIMUMPASSWORDLENGTH, message = "Password must be at least 8 characters long")
        String password,
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        LocalDate birthDate,
        @NotNull(message = "Balance cannot be null")
        @PositiveOrZero(message = "Balance cannot be negative")
        double balance
) {
    private static final int MINIMUMPASSWORDLENGTH = 8;
}

