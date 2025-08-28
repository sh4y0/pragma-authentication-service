package com.creditya.authservice.api.exception.model;


import com.creditya.authservice.usecase.authenticateuser.exception.BaseException;
import com.creditya.authservice.usecase.authenticateuser.utils.ErrorCatalog;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends BaseException {
    private static final ErrorCatalog error = ErrorCatalog.VALIDATION_EXCEPTION;

    public ValidationException(Map<String, String> errors) {
        super(
                error.getCode(),
                error.getTitle(),
                error.getMessage(),
                error.getStatus(),
                errors
        );
    }
}