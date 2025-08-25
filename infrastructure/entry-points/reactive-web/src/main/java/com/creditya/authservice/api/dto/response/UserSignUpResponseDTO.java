package com.creditya.authservice.api.dto.response;

import java.math.BigDecimal;

public record UserSignUpResponseDTO(String name,
                                    String lastName,
                                    String email,
                                    BigDecimal baseSalary) {
}
