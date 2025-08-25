package com.creditya.authservice.api.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

public record UserSignUpRequestDTO(@NotBlank(message = "Name cannot be blank")
                                    String name,

                                    @NotBlank(message = "Last name cannot be blank")
                                    String lastName,

                                    @NotBlank(message = "Email cannot be blank")
                                    @Email(message = "A valid email address is required")
                                    String email,

                                    @NotBlank(message = "Password cannot be blank")
                                    String password,
                                    String birthdate,
                                    String address,
                                    String phone,

                                    @NotNull(message = "Base salary cannot be null")
                                    @DecimalMin(value= "0.0", message = "Base salary cannot be less than 0")
                                    @DecimalMax(value = "15000000.0", message = "Base salary cannot exceed 15,000,000")
                                    BigDecimal baseSalary) {
}
