package com.creditya.authservice.api.mapper;

import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.api.dto.response.UserResponseDTO;
import com.creditya.authservice.api.dto.response.UserSignUpResponseDTO;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private UserMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    @DisplayName("Debería mapear UserSignUpRequestDTO a User")
    void dtoSignUpToDomain_shouldMapCorrectly() {
        UserSignUpRequestDTO dto = new UserSignUpRequestDTO(
                "John",
                "Doe",
                "61083587T",
                "1990-01-01",
                "1234567890",
                "123 Main St",
                "john.doe@example.com",
                "password123",
                new BigDecimal("1500.00")
        );

        User user = mapper.dtoSignUpToDomain(dto);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getDni()).isEqualTo("61083587T");
        assertThat(user.getBirthdate()).isEqualTo(LocalDate.parse("1990-01-01"));
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getAddress()).isEqualTo("123 Main St");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getBaseSalary()).isEqualByComparingTo("1500.00");
    }

    @Test
    @DisplayName("Debería mapear User a UserSignUpResponseDTO")
    void userToUserSignUpResponseDTO_shouldMapCorrectly() {
        User user = new User(
                UUID.randomUUID(),
                "Jane",
                "Smith",
                "98765432A",
                LocalDate.of(1985, 5, 20),
                "987654321",
                "456 Another St",
                "jane.smith@example.com",
                "securePass",
                new BigDecimal("2000.00"),
                UUID.randomUUID()
        );

        UserSignUpResponseDTO dto = mapper.userToUserSignUpResponseDTO(user);

        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("Jane");
        assertThat(dto.lastName()).isEqualTo("Smith");
        assertThat(dto.dni()).isEqualTo("98765432A");
        assertThat(dto.birthdate()).isEqualTo("1985-05-20");
        assertThat(dto.phone()).isEqualTo("987654321");
        assertThat(dto.address()).isEqualTo("456 Another St");
        assertThat(dto.email()).isEqualTo("jane.smith@example.com");
        assertThat(dto.baseSalary()).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("Debería mapear UserToken a TokenDTO")
    void domainToDto_shouldMapCorrectly() {
        UserToken token = new UserToken("jwt-access-token");

        TokenDTO dto = mapper.domainToDto(token);

        assertThat(dto).isNotNull();
        assertThat(dto.token()).isEqualTo("jwt-access-token");
    }

    @Test
    @DisplayName("Debería mapear User a UserResponseDTO")
    void userResponseDTO_shouldMapCorrectly() {
        User user = new User(
                UUID.randomUUID(),
                "Alice",
                "Brown",
                "12345678Z",
                LocalDate.of(1992, 3, 10),
                "555444333",
                "789 Street Name",
                "alice.brown@example.com",
                "superSecret",
                new BigDecimal("500.00"),
                UUID.randomUUID()
        );

        UserResponseDTO dto = mapper.userResponseDTO(user);

        assertThat(dto).isNotNull();
        assertThat(dto.name()).isEqualTo("Alice");
        assertThat(dto.lastName()).isEqualTo("Brown");
        assertThat(dto.email()).isEqualTo("alice.brown@example.com");
        assertThat(dto.phone()).isEqualTo("555444333");
    }
}
