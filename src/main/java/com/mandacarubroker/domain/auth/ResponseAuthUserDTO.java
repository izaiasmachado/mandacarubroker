package com.mandacarubroker.domain.auth;

public record ResponseAuthUserDTO(
        String token,
        int expiresIn,
        String tokenType
) {
}
