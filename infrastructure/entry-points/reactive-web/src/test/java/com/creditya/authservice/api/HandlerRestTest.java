package com.creditya.authservice.api;

import com.creditya.authservice.api.dto.request.UserLoginRequestDTO;
import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.api.exception.GlobalExceptionFilter;
import com.creditya.authservice.api.exception.model.UnexpectedException;
import com.creditya.authservice.api.exception.service.ValidationService;
import com.creditya.authservice.api.mapper.UserMapper;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.LoginUseCase;
import com.creditya.authservice.usecase.authenticateuser.SignUpUseCase;
import com.creditya.authservice.usecase.authenticateuser.exception.BaseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunctions;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@ExtendWith(MockitoExtension.class)
class HandlerRestTest {

    @Mock private UserMapper userMapper;
    @Mock private LoginUseCase loginUseCase;
    @Mock private SignUpUseCase signUpUseCase;
    @Mock private ValidationService validationService;
    @Mock private UseCaseLogger logger;

    @InjectMocks private HandlerAuth handlerAuth;

    private UserLoginRequestDTO loginRequest;
    private UserToken userToken;
    private TokenDTO tokenDTO;
    private UserSignUpRequestDTO signUpRequest;
    private User userDomain;
    private GlobalExceptionFilter globalExceptionFilter;

    @BeforeEach
    void setUp() {
        loginRequest = new UserLoginRequestDTO("test@example.com", "password");
        userToken = new UserToken("some_jwt_token");
        tokenDTO = new TokenDTO("some_jwt_token");
        signUpRequest = new UserSignUpRequestDTO(
                "John", "Doe", "1990-01-01", "1234567890", "123 Main St",
                "john.doe@example.com", "password", new BigDecimal("1000.0")
        );
        userDomain = new User(UUID.randomUUID(), "John", "Doe",
                LocalDate.parse("1990-01-01"), "1234567890", "123 Main St",
                "john.doe@example.com", "password", new BigDecimal("1000.0"), UUID.randomUUID());

        globalExceptionFilter = new GlobalExceptionFilter(logger);
    }

    private MockServerRequest mockRequest(Object body) {
        return MockServerRequest.builder().body(Mono.just(body));
    }

    private void verifyLoginInteractions() {
        verify(validationService).validate(loginRequest);
        verify(loginUseCase).authenticate(loginRequest.email(), loginRequest.password());
        verify(userMapper).domainToDto(userToken);
    }

    private void verifySignUpInteractions() {
        verify(validationService).validate(signUpRequest);
        verify(userMapper).dtoSignUpToDomain(signUpRequest);
        verify(signUpUseCase).signUp(userDomain);
    }

    @Test
    @DisplayName("Should successfully login a user and return a token")
    void logIn_success() {
        when(validationService.validate(loginRequest)).thenReturn(Mono.just(loginRequest));
        when(loginUseCase.authenticate(loginRequest.email(), loginRequest.password())).thenReturn(Mono.just(userToken));
        when(userMapper.domainToDto(userToken)).thenReturn(tokenDTO);

        StepVerifier.create(handlerAuth.logIn(mockRequest(loginRequest)))
                .assertNext(res -> {
                    assertEquals(HttpStatus.OK, res.statusCode());
                    assertEquals(MediaType.APPLICATION_JSON, res.headers().getContentType());
                })
                .verifyComplete();

        verifyLoginInteractions();
    }

    @Test
    @DisplayName("Should return BaseException on login failure")
    void logIn_baseException() {
        BaseException ex = new BaseException("AUTH-INVALID-CREDENTIALS",
                "Invalid credentials", "The provided credentials are incorrect", 401, Map.of("credentials","Invalid email or password"));

        when(validationService.validate(loginRequest)).thenReturn(Mono.just(loginRequest));
        when(loginUseCase.authenticate(loginRequest.email(), loginRequest.password())).thenReturn(Mono.error(ex));

        StepVerifier.create(handlerAuth.logIn(mockRequest(loginRequest)))
                .expectErrorMatches(t -> t instanceof BaseException && ((BaseException) t).getStatus() == ex.getStatus())
                .verify();

        verify(validationService).validate(loginRequest);
        verify(loginUseCase).authenticate(loginRequest.email(), loginRequest.password());
    }

    @Test
    @DisplayName("Should return UnexpectedException on unexpected login error")
    void logIn_unexpectedException() {
        RuntimeException ex = new RuntimeException("Oops");

        when(validationService.validate(loginRequest)).thenReturn(Mono.just(loginRequest));
        when(loginUseCase.authenticate(loginRequest.email(), loginRequest.password())).thenReturn(Mono.error(ex));

        StepVerifier.create(handlerAuth.logIn(mockRequest(loginRequest)))
                .expectErrorMatches(t -> t instanceof UnexpectedException && t.getCause() == ex)
                .verify();

        verify(validationService).validate(loginRequest);
        verify(loginUseCase).authenticate(loginRequest.email(), loginRequest.password());
    }

    @Test
    @DisplayName("Should successfully sign up a user and return 201")
    void signUp_success() {
        when(validationService.validate(signUpRequest)).thenReturn(Mono.just(signUpRequest));
        when(userMapper.dtoSignUpToDomain(signUpRequest)).thenReturn(userDomain);
        when(signUpUseCase.signUp(userDomain)).thenReturn(Mono.empty());

        StepVerifier.create(handlerAuth.signUp(mockRequest(signUpRequest)))
                .expectNextMatches(res -> {
                    assertEquals(HttpStatus.CREATED, res.statusCode());
                    assertEquals(URI.create("/api/v1/users"), res.headers().getLocation());
                    return true;
                })
                .expectComplete()
                .verify();

        verifySignUpInteractions();
    }

    @Test
    @DisplayName("Should return BaseException on signup failure")
    void signUp_baseException() {
        BaseException ex = new BaseException("AUTH-USER-ALREADY-EXISTS",
                "User already exists", "User with this email already exists", 409, Map.of("email","Already in use"));

        when(validationService.validate(signUpRequest)).thenReturn(Mono.just(signUpRequest));
        when(userMapper.dtoSignUpToDomain(signUpRequest)).thenReturn(userDomain);
        when(signUpUseCase.signUp(userDomain)).thenReturn(Mono.error(ex));

        StepVerifier.create(handlerAuth.signUp(mockRequest(signUpRequest)))
                .expectErrorMatches(t -> t instanceof BaseException && ((BaseException)t).getStatus() == ex.getStatus())
                .verify();

        verifySignUpInteractions();
    }

    @Test
    @DisplayName("Should return UnexpectedException on unexpected signup error")
    void signUp_unexpectedException() {
        RuntimeException ex = new RuntimeException("DB lost");

        when(validationService.validate(signUpRequest)).thenReturn(Mono.just(signUpRequest));
        when(userMapper.dtoSignUpToDomain(signUpRequest)).thenReturn(userDomain);
        when(signUpUseCase.signUp(userDomain)).thenReturn(Mono.error(ex));

        StepVerifier.create(handlerAuth.signUp(mockRequest(signUpRequest)))
                .expectErrorMatches(t -> t instanceof UnexpectedException && t.getCause() == ex)
                .verify();

        verifySignUpInteractions();
    }

    @Test
    @DisplayName("handleInvalidFormat -> returns 400 with proper body")
    void handleInvalidFormat() {
        InvalidFormatException invalidFormat = new InvalidFormatException(null,"Invalid value","abc",Integer.class);
        invalidFormat.prependPath(new Object(), "age");

        UnexpectedException ex = Mockito.mock(UnexpectedException.class);
        Mockito.when(ex.getCause()).thenReturn(invalidFormat);

        WebTestClient client = WebTestClient.bindToRouterFunction(
                RouterFunctions.route(POST("/api/v1/users"), req -> Mono.error(ex))
                        .filter(globalExceptionFilter)
        ).build();

        client.post().uri("/api/v1/users")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_REQUEST_FORMAT")
                .jsonPath("$.tittle").isEqualTo("Invalid Request Format")
                .jsonPath("$.message").isEqualTo("Invalid value 'abc' for field 'age'")
                .jsonPath("$.errors.field").isEqualTo("age")
                .jsonPath("$.errors.invalidValue").isEqualTo("abc")
                .jsonPath("$.errors.expectedType").isEqualTo("Integer");
    }

    @Test
    @DisplayName("handleGenericException -> returns 500 with GENERIC_ERROR")
    void handleGenericException() {
        RuntimeException ex = new RuntimeException("Something went wrong");

        WebTestClient client = WebTestClient.bindToRouterFunction(
                RouterFunctions.route(POST("/api/v1/users"), req -> Mono.error(ex))
                        .filter(globalExceptionFilter)
        ).build();

        client.post().uri("/api/v1/users")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("UNEXPECTED_ERROR")
                .jsonPath("$.tittle").isEqualTo("Unexpected Error")
                .jsonPath("$.message").isEqualTo("An unexpected error occurred. Please try again later.");
    }

    @Test
    @DisplayName("signInDoc returns empty Mono")
    void signInDoc() {
        StepVerifier.create(handlerAuth.signInDoc(loginRequest))
                .expectComplete()
                .verify();
    }
}
