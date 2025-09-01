package com.creditya.authservice.usecase.authenticateuser.exception;

import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class UsersNotFoundException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.USER_ALREADY_EXISTS;

    public UsersNotFoundException() {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                error.getErrors()
        );
    }
}
