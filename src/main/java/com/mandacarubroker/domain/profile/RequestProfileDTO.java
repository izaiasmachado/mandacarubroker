package com.mandacarubroker.domain.profile;

import com.mandacarubroker.domain.user.MinimumAge;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RequestProfileDTO(
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
        LocalDate birthDate
) {
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MINIMUM_AGE = 18;
}

