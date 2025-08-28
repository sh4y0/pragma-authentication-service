package com.creditya.authservice.api.config.mapper;

import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.api.mapper.UserMapper;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("Should map UserSignUpRequestDTO to User entity correctly")
    void testToEntity() {
        UserSignUpRequestDTO dto = new UserSignUpRequestDTO(
                "John",
                "Doe",
                "1995-08-25",
                "987654321",
                "Av. Siempre Viva 123",
                "john@example.com",
                "password123",
                new BigDecimal("2500.0")
        );

        User user = mapper.dtoSignUpToDomain(dto);

        assertNotNull(user);
        assertEquals(dto.name(), user.getName());
        assertEquals(dto.lastName(), user.getLastName());
        assertEquals(dto.email(), user.getEmail());
        assertEquals(dto.password(), user.getPassword());
        assertEquals(LocalDate.parse(dto.birthdate()), user.getBirthdate());
        assertEquals(dto.address(), user.getAddress());
        assertEquals(dto.phone(), user.getPhone());
        assertEquals(dto.baseSalary(), user.getBaseSalary());
    }

    @Test
    @DisplayName("Should map User entity to SignUpResponse correctly")
    void testToResponse() {
        UserToken user = UserToken.builder()
                .token("mocked-token-550e8400-e29b-41d4-a716-446655440000")
                .build();

        TokenDTO response = mapper.domainToDto(user);

        assertNotNull(response);
        assertEquals(user.token(), response.token());
    }


    @Test
    @DisplayName("Should return null when mapping null values")
    void testNullHandling() {
        assertNull(mapper.dtoSignUpToDomain(null));
        assertNull(mapper.domainToDto(null));
    }
}
