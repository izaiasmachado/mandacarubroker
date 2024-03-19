package com.mandacarubroker.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpStatusMessageProvider {
    public NotFoundException(final String receivedMessage) {
        super(receivedMessage);
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
