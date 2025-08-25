package com.creditya.authservice.api;

import com.creditya.authservice.api.dto.request.UserLoginRequestDTO;
import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.exception.model.UnexpectedException;
import com.creditya.authservice.api.exception.service.ValidationService;
import com.creditya.authservice.api.mapper.UserMapper;
import com.creditya.authservice.usecase.authenticateuser.LoginUseCase;
import com.creditya.authservice.usecase.authenticateuser.SignUpUseCase;
import com.creditya.authservice.usecase.authenticateuser.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UserMapper userMapper;
    private final LoginUseCase loginUseCase;
    private final SignUpUseCase signUpUseCase;
    private final ValidationService validationService;

    public Mono<ServerResponse> logIn(ServerRequest request) {
        return request.bodyToMono(UserLoginRequestDTO.class)
                .flatMap(validationService::validate)
                .flatMap( requestDto -> loginUseCase.authenticate(requestDto.email(), requestDto.password()))
                .map(userMapper::domainToDto)
                .flatMap(tokenDto -> ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(tokenDto))
                .onErrorResume(ex -> Mono.error(
                        ex instanceof BaseException ? ex : new UnexpectedException(ex)
                ));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_ADVISER')")
    public Mono<ServerResponse> signUp(ServerRequest request) {
        return request.bodyToMono(UserSignUpRequestDTO.class)
                .flatMap(validationService::validate)
                .map(userMapper::dtoSignUpToDomain)
                .flatMap(signUpUseCase::signUp)
                .map(userMapper::domainToDtoSignUp)
                .flatMap(responseDto -> ServerResponse
                        .created(URI.create("/api/v1/users"))
                        .build())
                .onErrorResume(ex -> Mono.error(
                        ex instanceof BaseException ? ex : new UnexpectedException(ex)
                ));
    }
}
