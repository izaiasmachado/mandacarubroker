package com.mandacarubroker.exceptions;

import org.springframework.http.HttpStatus;

public abstract class HttpStatusMessageProvider extends RuntimeException {
    private final String message;

    public HttpStatusMessageProvider(final String receivedMessage) {
        this.message = receivedMessage;
    }

    public String getMessage() {
        return message;
    }

    public abstract HttpStatus getHttpStatus();
}
