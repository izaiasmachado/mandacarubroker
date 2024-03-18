package com.mandacarubroker.exceptions;

public record ErrorResponse(
        int status,
        String message
) {
    public static ErrorResponse fromException(final HttpStatusMessageProvider ex) {
        return new ErrorResponse(ex.getHttpStatus().value(), ex.getMessage());
    }
}
