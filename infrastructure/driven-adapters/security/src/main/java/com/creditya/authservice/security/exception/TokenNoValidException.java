package com.creditya.authservice.security.exception;


import com.creditya.authservice.usecase.authenticateuser.exception.BaseException;
import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;

public class TokenNoValidException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.TOKEN_INVALID;

    public TokenNoValidException() {
        super(error.getCode(), error.getTitle(), error.getMessage(), error.getStatus(), error.getErrors());
    }
}
