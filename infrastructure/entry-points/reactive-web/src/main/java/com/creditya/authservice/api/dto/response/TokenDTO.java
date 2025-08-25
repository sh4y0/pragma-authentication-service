package com.creditya.authservice.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenDTO", description = "Response token after user authentication")
public record TokenDTO(
        @Schema(description = "JWT token", example = "eyJhbG...")
        String token) {
}
