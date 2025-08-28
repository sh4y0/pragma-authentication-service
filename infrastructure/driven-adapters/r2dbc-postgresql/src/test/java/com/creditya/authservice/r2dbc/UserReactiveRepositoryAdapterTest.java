package com.creditya.authservice.r2dbc;

import com.creditya.authservice.model.user.User;
import com.creditya.authservice.r2dbc.entity.UserEntity;
import com.creditya.authservice.r2dbc.user_adapter.UserReactiveRepository;
import com.creditya.authservice.r2dbc.user_adapter.UserReactiveRepositoryGatewayAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UserReactiveRepositoryGatewayAdapter adapter;

    private User testUser;
    private UserEntity testUserEntity;
    private final UUID testUserId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID testRoleId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {

        testUser = User.builder()
                .userId(testUserId)
                .name("Jhon")
                .lastName("Doe")
                .birthdate(LocalDate.of(1997, 2, 2))
                .phone("98988787")
                .address("test address")
                .email(testEmail)
                .password("hashedpassword")
                .baseSalary(new BigDecimal(5000))
                .roleId(testRoleId)
                .build();

        testUserEntity = UserEntity.builder()
                .userId(testUserId)
                .name("Jhon")
                .lastName("Doe")
                .birthdate(LocalDate.of(1997, 2, 2))
                .phone("98988787")
                .address("test address")
                .email(testEmail)
                .password("hashedpassword")
                .baseSalary(new BigDecimal(5000))
                .roleId(testRoleId)
                .build();
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void shouldFindUserByEmailSuccessfully() {
        when(repository.findByEmail(testEmail)).thenReturn(Mono.just(testUserEntity));
        when(mapper.map(testUserEntity, User.class)).thenReturn(testUser);

        StepVerifier.create(adapter.findByEmail(testEmail))
                .expectNext(testUser)
                .verifyComplete();

        verify(repository).findByEmail(testEmail);
        verify(mapper).map(testUserEntity, User.class);
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        when(repository.findByEmail(testEmail)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByEmail(testEmail))
                .verifyComplete();

        verify(repository).findByEmail(testEmail);
        verify(mapper, never()).map(any(), eq(User.class));
    }

    @Test
    @DisplayName("Should handle error when finding user by email")
    void shouldHandleErrorWhenFindingUserByEmail() {
        RuntimeException exception = new RuntimeException("Database error");
        when(repository.findByEmail(testEmail)).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.findByEmail(testEmail))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail(testEmail);
        verify(mapper, never()).map(any(), eq(User.class));
    }

    @Test
    @DisplayName("Should sign up user successfully")
    void shouldSignUpUserSuccessfully() {
        when(mapper.map(testUser, UserEntity.class)).thenReturn(testUserEntity);
        when(repository.save(testUserEntity)).thenReturn(Mono.just(testUserEntity));

        StepVerifier.create(adapter.signUp(testUser))
                .expectNext(testUser)
                .verifyComplete();

        verify(mapper).map(testUser, UserEntity.class);
        verify(repository).save(testUserEntity);
    }

    @Test
    @DisplayName("Should handle error when signing up user")
    void shouldHandleErrorWhenSigningUpUser() {
        RuntimeException exception = new RuntimeException("Save error");
        when(mapper.map(testUser, UserEntity.class)).thenReturn(testUserEntity);
        when(repository.save(testUserEntity)).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.signUp(testUser))
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).map(testUser, UserEntity.class);
        verify(repository).save(testUserEntity);
        verify(mapper, never()).map(testUserEntity, User.class);
    }

    @Test
    @DisplayName("Should handle mapping error in findByEmail")
    void shouldHandleMappingErrorInFindByEmail() {
        RuntimeException mappingException = new RuntimeException("Mapping error");
        when(repository.findByEmail(testEmail)).thenReturn(Mono.just(testUserEntity));
        when(mapper.map(testUserEntity, User.class)).thenThrow(mappingException);

        StepVerifier.create(adapter.findByEmail(testEmail))
                .expectError(RuntimeException.class)
                .verify();

        verify(repository).findByEmail(testEmail);
        verify(mapper).map(testUserEntity, User.class);
    }

    @Test
    @DisplayName("Should handle mapping error in signUp")
    void shouldHandleMappingErrorInSignUp() {
        RuntimeException mappingException = new RuntimeException("Mapping error");
        when(mapper.map(testUser, UserEntity.class)).thenThrow(mappingException);

        StepVerifier.create(adapter.signUp(testUser))
                .expectError(RuntimeException.class)
                .verify();

        verify(mapper).map(testUser, UserEntity.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle null email in findByEmail")
    void shouldHandleNullEmailInFindByEmail() {
        when(repository.findByEmail(null)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByEmail(null))
                .verifyComplete();

        verify(repository).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle empty email in findByEmail")
    void shouldHandleEmptyEmailInFindByEmail() {
        String emptyEmail = "";
        when(repository.findByEmail(emptyEmail)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByEmail(emptyEmail))
                .verifyComplete();

        verify(repository).findByEmail(emptyEmail);
    }
}
