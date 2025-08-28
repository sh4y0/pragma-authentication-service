package com.creditya.authservice.api.config;

import com.creditya.authservice.api.Handler;
import com.creditya.authservice.api.RouterRest;
import com.creditya.authservice.api.exception.GlobalExceptionFilter;
import com.creditya.authservice.api.dto.request.UserLoginRequestDTO;
import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.LoginUseCase;
import com.creditya.authservice.usecase.authenticateuser.SignUpUseCase;
import com.creditya.authservice.api.exception.service.ValidationService;
import com.creditya.authservice.api.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionFilter.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private SignUpUseCase signUpUseCase;

    @MockitoBean
    private ValidationService validationService;

    @MockitoBean
    private UseCaseLogger useCaseLogger;

    private UserSignUpRequestDTO signUpRequest;
    private UserLoginRequestDTO loginRequest;
    private TokenDTO tokenDTO;

    @BeforeEach
    void setUp() {
        signUpRequest = new UserSignUpRequestDTO("John",
                "Doe",
                "1990-01-01",
                "1234567890",
                "123 Main St",
                "john.doe@example.com",
                "password123",
                new BigDecimal("1000.00"));

        loginRequest = new UserLoginRequestDTO("john.doe@example.com", "password123");
        tokenDTO = new TokenDTO("access-token");

        Handler handler = new Handler(userMapper, loginUseCase, signUpUseCase, validationService);
        webTestClient = WebTestClient.bindToRouterFunction(
                new RouterRest().routerFunction(handler, new GlobalExceptionFilter(useCaseLogger))
        ).build();
    }

    @Test
    void givenValidSignUpRequest_whenSignUp_thenReturnsCreated() {
        when(validationService.validate(any(UserSignUpRequestDTO.class)))
                .thenReturn(Mono.just(signUpRequest));
        when(userMapper.dtoSignUpToDomain(any(UserSignUpRequestDTO.class)))
                .thenReturn(new User());
        when(signUpUseCase.signUp(any()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void givenValidLoginRequest_whenLogin_thenReturnsTokenDTO() {
        when(validationService.validate(any(UserLoginRequestDTO.class)))
                .thenReturn(Mono.just(loginRequest));
        Mockito.when(loginUseCase.authenticate(any(String.class), any(String.class)))
                .thenReturn(Mono.just(new UserToken(tokenDTO.token())));
        when(userMapper.domainToDto(any())).thenReturn(tokenDTO);

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDTO.class)
                .value(response -> {
                    assert response.token().equals("access-token");
                });
    }

    @Test
    void givenInvalidSignUpRequest_whenValidationFails_thenReturnsServerError() {
        when(validationService.validate(any(UserSignUpRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Validation failed")));

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void givenInvalidLoginRequest_whenAuthenticationFails_thenReturnsServerError() {
        when(validationService.validate(any(UserLoginRequestDTO.class)))
                .thenReturn(Mono.just(loginRequest));
        when(loginUseCase.authenticate(any(String.class), any(String.class)))
                .thenReturn(Mono.error(new RuntimeException("Invalid credentials")));

        webTestClient.post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
