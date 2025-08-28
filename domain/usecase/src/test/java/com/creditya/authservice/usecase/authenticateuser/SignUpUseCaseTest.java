package com.creditya.authservice.usecase.authenticateuser;


import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.PasswordHasherGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.TransactionalGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.exception.RoleNotFoundException;
import com.creditya.authservice.usecase.authenticateuser.exception.UserAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignUpUseCaseTest {

    @Mock
    private UserRepositoryGateway userRepositoryGateway;
    @Mock
    private RoleRepositoryGateway roleRepository;
    @Mock
    private PasswordHasherGateway passwordHasher;
    @Mock
    private TransactionalGateway transactionalGateway;
    @Mock
    private UseCaseLogger useCaseLogger;

    @InjectMocks
    private SignUpUseCase signUpUseCase;

    private User sampleUser;
    private String plainPassword;
    private String hashedPassword;
    private UUID roleId;

    @BeforeEach
    void setup() {
        plainPassword = "plainPassword";
        hashedPassword = "hashedPassword";
        roleId = UUID.randomUUID();
        sampleUser = User.builder()
                .email("test@example.com")
                .password(plainPassword)
                .build();
    }

    @Test
    @DisplayName("Should sign up user successfully")
    void signUp_success() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Mono.just(new Role(roleId, "CUSTOMER", "Customer role")));
        when(passwordHasher.hash(plainPassword)).thenReturn(hashedPassword);
        when(userRepositoryGateway.signUp(any(User.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(transactionalGateway.executeInTransaction(any())).thenAnswer(invocation -> (invocation.getArgument(0)));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getPassword().equals(hashedPassword) &&
                                user.getRoleId().equals(roleId)
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when user already exists")
    void signUp_userAlreadyExists() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.just(sampleUser));
        when(transactionalGateway.executeInTransaction(any())).thenAnswer(invocation -> (invocation.getArgument(0)));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectError(UserAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when CUSTOMER role not found")
    void signUp_roleNotFound() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Mono.empty());
        when(transactionalGateway.executeInTransaction(any())).thenAnswer(invocation -> (invocation.getArgument(0)));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectError(RoleNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should propagate exception if password hashing fails")
    void signUp_passwordHasherThrows() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Mono.just(new Role(roleId, "CUSTOMER", "Customer role")));
        when(passwordHasher.hash(plainPassword)).thenThrow(new RuntimeException("Hash failed"));
        when(transactionalGateway.executeInTransaction(any())).thenAnswer(invocation -> (invocation.getArgument(0)));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Hash failed"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate exception if userRepository.signUp fails")
    void signUp_userRepositoryThrows() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.empty());
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Mono.just(new Role(roleId, "CUSTOMER", "Customer role")));
        when(passwordHasher.hash(plainPassword)).thenReturn(hashedPassword);
        when(userRepositoryGateway.signUp(any(User.class))).thenReturn(Mono.error(new RuntimeException("DB error")));
        when(transactionalGateway.executeInTransaction(any())).thenAnswer(invocation -> (invocation.getArgument(0)));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("DB error"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate exception if transaction fails")
    void signUp_transactionFails() {
        when(userRepositoryGateway.findByEmail(sampleUser.getEmail())).thenReturn(Mono.empty());
        when(transactionalGateway.executeInTransaction(any())).thenReturn(Mono.error(new RuntimeException("Transaction failed")));

        Mono<User> result = signUpUseCase.signUp(sampleUser);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().equals("Transaction failed"))
                .verify();
    }
}
