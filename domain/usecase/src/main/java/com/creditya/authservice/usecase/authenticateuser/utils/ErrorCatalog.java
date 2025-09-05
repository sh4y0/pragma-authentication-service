package com.creditya.authservice.usecase.authenticateuser.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public enum ErrorCatalog {

    FORBIDDEN(
            "FORBIDDEN",
            "Forbidden",
            "You do not have permission to access this resource.",
            403,
            Map.of("auth", "Access denied - insufficient permissions")
    ),

    UNAUTHORIZED(
            "UNAUTHORIZED",
            "Unauthorized",
            "Authentication failed. Please provide a valid token.",
            401,
            Map.of("auth", "Missing, invalid, or expired authentication token")
    ),

    USERS_NOT_FOUND(
            "USERS-NOT-FOUND",
            "Users Not Found",
            "One or more users could not be retrieved from Auth service",
            404,
            Map.of("userIds", "List of user IDs not found")
    ),
    TOKEN_INVALID("AUTH-TOKEN-CREDENTIALS",
            "Token Invalid",
            "The token is invalid",
            401,
            Map.of("token", "The token is invalid")


    ),
    INVALID_CREDENTIALS(
            "AUTH-INVALID-CREDENTIALS",
            "Invalid Credentials",
            "The provided credentials are incorrect",
            401,
            Map.of("credentials", "Invalid email or password")
    ),
    ROLE_NOT_FOUND(
            "AUTH-ROLE-NOT-FOUND",
            "Role Not Found",
            "The specified role does not exist",
            404,
            Map.of("role", "The specified role does not exist")
    ),
    USER_ALREADY_EXISTS(
            "AUTH-USER-ALREADY-EXISTS",
            "User already exists",
            "User with the provided email already exists",
            409,
            Map.of("email", "This email is already in use")
    ),
    VALIDATION_EXCEPTION(
            "VALIDATION_EXCEPTION",
            "Validation Failed",
            "Oops! Some of the data you sent doesn’t look right. Please review the fields and try again.",
            400,
            null
    ),
    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            "Internal Server Error",
            "Something went wrong on our side. Please try again later or contact support if the issue persists.",
            500,
            Map.of("server", "Unexpected error occurred")
    );

    private final String code;
    private final String title;
    private final String message;
    private final int status;
    private final Map<String, String> errors;

}
