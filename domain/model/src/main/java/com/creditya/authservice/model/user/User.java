package com.creditya.authservice.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private UUID userId;
    private String name;
    private String lastName;
    private LocalDate birthdate;
    private String phone;
    private String address;
    private String email;
    private String password;
    private BigDecimal baseSalary;
    private UUID roleId;

}
