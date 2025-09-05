package com.creditya.authservice.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

public record UserSignUpResponseDTO(
        @Schema(description = "User's Name", example = "Rodri")
        String name,

        @Schema(description = "User's LastName", example = "Gutierrez")
        String lastName,

        @Schema(description = "User's DNI", example = "61070032")
        String dni,

        @Schema(description = "User's birthday", example = "1997-05-18")
        String birthdate,

        @Schema(description = "User's phone number", example = "95655256")
        String phone,

        @Schema(description = "User's address", example = "Miraflores, Lima, Peru")
        String address,

        @Schema(description = "User's Email", example = "gutierrezherrada@gmail.com")
        String email,

        @Schema(description = "User's base salary", example = "4900.50")
        BigDecimal baseSalary
) {
}
