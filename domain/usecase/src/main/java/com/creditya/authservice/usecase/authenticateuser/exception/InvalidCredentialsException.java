package com.creditya.authservice.usecase.authenticateuser.exception;

import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class InvalidCredentialsException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.INVALID_CREDENTIALS;

    public InvalidCredentialsException() {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                error.getErrors()
        );
    }
}