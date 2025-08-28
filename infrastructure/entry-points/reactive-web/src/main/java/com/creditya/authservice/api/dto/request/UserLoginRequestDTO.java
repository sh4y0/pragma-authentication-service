package com.creditya.authservice.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(name = "UserLoginRequestDTO", description = "Data required for user login")
public record UserLoginRequestDTO(
        @NotBlank(message = "Email cannot be blank")
        @Pattern(
                regexp = "^(?!.*\\.\\.)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Email format is invalid"
        )
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Schema(description = "User's password", example = "********")
        String password) {
}
