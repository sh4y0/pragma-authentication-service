package com.creditya.authservice.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "UserSignUpRequestDTO", description = "Data required for user registration")
public record UserSignUpRequestDTO(
                                   @NotBlank(message = "Name cannot be blank")
                                   @Schema(description = "User's Name", example = "Rodri")
                                   String name,
                                   @Schema(description = "User's LastName", example = "Gutierrez")
                                    @NotBlank(message = "Last name cannot be blank")
                                    String lastName,
                                   @Schema(description = "User's birthday", example = "1997-05-18")
                                    LocalDate birthdate,
                                   @Schema(description = "User's phone number", example = "95655256")
                                    String phone,
                                   @Schema(description = "User's address", example = "Miraflores, Lima, Peru")
                                    String address,

                                    @NotBlank(message = "Email cannot be blank")
                                    @Email(message = "A valid email address is required")
                                   @Schema(description = "User's Email", example = "gutierrezherrada@gmail.com")
                                    String email,

                                    @NotBlank(message = "Password cannot be blank")
                                   @Schema(description = "User's password", example = "********")
                                    String password,


                                    @NotNull(message = "Base salary cannot be null")
                                    @DecimalMin(value= "0.0", message = "Base salary cannot be less than 0")
                                    @DecimalMax(value = "15000000.0", message = "Base salary cannot exceed 15,000,000")
                                   @Schema(description = "User's base salary", example = "4900.50")
                                    BigDecimal baseSalary) {
}
