package com.creditya.authservice.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity{
    @Id
    @Column("user_id")
    private UUID userId;
    private String name;
    private String lastName;
    private LocalDate birthdate;
    private String phone;
    private String address;
    private String email;
    private String password;
    private BigDecimal baseSalary;
    @Column("role_id")
    private UUID roleId;

}
