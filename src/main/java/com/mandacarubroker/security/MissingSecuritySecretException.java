package com.mandacarubroker.security;

public class MissingSecuritySecretException extends RuntimeException {
    public MissingSecuritySecretException(final String message) {
        super("Missing security secret: " + message);
    }
}
