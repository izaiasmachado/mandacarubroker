package com.mandacarubroker.security;

public final class SecuritySecrets {
    private SecuritySecrets() {
    }

    public static String getJWTSecret() {
        final String secret = System.getenv("MANDACARU_JWT_SECRET");

        if (secret == null) {
            throw new MissingSecuritySecretException("JWT secret not found");
        }

        return secret;
    }
}
