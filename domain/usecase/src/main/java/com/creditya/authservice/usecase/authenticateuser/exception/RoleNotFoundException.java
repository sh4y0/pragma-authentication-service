package com.creditya.authservice.usecase.authenticateuser.exception;

import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class RoleNotFoundException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.ROLE_NOT_FOUND;

    public RoleNotFoundException() {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                error.getErrors()
        );
    }
}