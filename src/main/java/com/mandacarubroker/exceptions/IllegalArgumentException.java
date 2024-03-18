package com.mandacarubroker.exceptions;

import org.springframework.http.HttpStatus;

public class IllegalArgumentException extends HttpStatusMessageProvider {
    public IllegalArgumentException(final String receivedMessage) {
        super(receivedMessage);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
