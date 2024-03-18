package com.mandacarubroker.exceptions;

import org.springframework.http.HttpStatus;

public abstract class HttpStatusMessageProvider extends RuntimeException {
    private final String message;

    protected HttpStatusMessageProvider(final String receivedMessage) {
        this.message = receivedMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public abstract HttpStatus getHttpStatus();
}
