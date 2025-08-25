package com.creditya.authservice.usecase.authenticateuser.exception;

import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class UserAlreadyExistsException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.USER_ALREADY_EXISTS;

    public UserAlreadyExistsException() {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                error.getErrors()
        );
    }
}