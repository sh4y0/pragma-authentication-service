package com.creditya.authservice.api.exception.model;


import com.creditya.authservice.usecase.authenticateuser.exception.BaseException;
import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class UnexpectedException extends BaseException {
    private final static ErrorCatalog error = ErrorCatalog.INTERNAL_SERVER_ERROR;

    public UnexpectedException(Throwable cause) {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                error.getErrors()
        );
        initCause(cause);
    }
}