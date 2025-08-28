package com.creditya.authservice.usecase.authenticateuser;

import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.PasswordHasherGateway;
import com.creditya.authservice.model.user.gateways.TokenGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.exception.InvalidCredentialsException;
import com.creditya.authservice.usecase.authenticateuser.exception.RoleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepositoryGateway userRepositoryGateway;
    @Mock
    private RoleRepositoryGateway roleRepositoryGateway;
    @Mock
    private TokenGateway tokenGateway;
    @Mock
    private PasswordHasherGateway passwordHasherGateway;
    @Mock
    private UseCaseLogger useCaseLogger;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User sampleUser;
    private Role sampleRole;
    private String email;
    private String password ;
    private String hashedPassword;
    private String token;
    private UUID roleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        password = "plainPassword";
        hashedPassword = "hashedPassword";
        roleId = UUID.randomUUID();
        token = "jwtToken";

        sampleUser = User.builder()
                .userId(UUID.randomUUID())
                .name("Jhon")
                .lastName("Doe")
                .birthdate(LocalDate.parse("1997-05-18"))
                .phone("123456789")
                .address("123 Main St")
                .email(email)
                .password(hashedPassword)
                .baseSalary(BigDecimal.valueOf(25000.00))
                .roleId(roleId)
                .build();

        sampleRole = Role.builder()
                .roleId(roleId)
                .name("CUSTOMER")
                .description("Customer role")
                .build();
    }

    @Test
    @DisplayName("Should authenticate successfully and return a valid token")
    void authenticate_success() {
        when(userRepositoryGateway.findByEmail(email)).thenReturn(Mono.just(sampleUser));
        when(passwordHasherGateway.matches(password, hashedPassword)).thenReturn(true);
        when(roleRepositoryGateway.findByRoleId(roleId)).thenReturn(Mono.just(sampleRole));
        when(tokenGateway.generateToken(sampleUser, sampleRole)).thenReturn(token);

        StepVerifier.create(loginUseCase.authenticate(email, password))
                .expectNextMatches(userToken -> userToken.token().equals(token))
                .verifyComplete();

        verify(userRepositoryGateway).findByEmail(email);
        verify(passwordHasherGateway).matches(password, hashedPassword);
        verify(roleRepositoryGateway).findByRoleId(roleId);
        verify(tokenGateway).generateToken(sampleUser, sampleRole);
    }

    @Test
    @DisplayName("Should fail when user is not found")
    void authenticate_userNotFound() {
        when(userRepositoryGateway.findByEmail(email)).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.authenticate(email, password))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(userRepositoryGateway).findByEmail(email);
        verifyNoInteractions(passwordHasherGateway, roleRepositoryGateway, tokenGateway);
    }

    @Test
    @DisplayName("Should fail when password is invalid")
    void authenticate_invalidPassword() {
        when(userRepositoryGateway.findByEmail(email)).thenReturn(Mono.just(sampleUser));
        when(passwordHasherGateway.matches(password, hashedPassword)).thenReturn(false);

        StepVerifier.create(loginUseCase.authenticate(email, password))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(userRepositoryGateway).findByEmail(email);
        verify(passwordHasherGateway).matches(password, hashedPassword);
        verifyNoInteractions(roleRepositoryGateway, tokenGateway);
    }

    @Test
    @DisplayName("Should fail when role is not found")
    void authenticate_roleNotFound() {
        when(userRepositoryGateway.findByEmail(email)).thenReturn(Mono.just(sampleUser));
        when(passwordHasherGateway.matches(password, hashedPassword)).thenReturn(true);
        when(roleRepositoryGateway.findByRoleId(roleId)).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.authenticate(email, password))
                .expectError(RoleNotFoundException.class)
                .verify();

        verify(userRepositoryGateway).findByEmail(email);
        verify(passwordHasherGateway).matches(password, hashedPassword);
        verify(roleRepositoryGateway).findByRoleId(roleId);
        verifyNoInteractions(tokenGateway);
    }


}
