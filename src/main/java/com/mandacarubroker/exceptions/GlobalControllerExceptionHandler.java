package com.mandacarubroker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ExceptionHandler(HttpStatusMessageProvider.class)
    public ResponseEntity<Object> handleHttpStatusMessageProviderException(final HttpStatusMessageProvider ex) {
        HttpStatus status = ex.getHttpStatus();
        String message = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}
