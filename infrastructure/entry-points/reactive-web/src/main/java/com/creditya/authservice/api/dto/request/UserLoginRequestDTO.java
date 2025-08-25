package com.creditya.authservice.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "A valid email address is required")
        String email,
        @NotBlank(message = "Password cannot be blank")
        String password) {
}
