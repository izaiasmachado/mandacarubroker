package com.mandacarubroker.domain.auth;

import jakarta.validation.constraints.NotBlank;

public record RequestAuthUserDTO(
        @NotBlank(message = "Username is required")
        String username,
        @NotBlank(message = "Password is required")
        String password
) {
}
