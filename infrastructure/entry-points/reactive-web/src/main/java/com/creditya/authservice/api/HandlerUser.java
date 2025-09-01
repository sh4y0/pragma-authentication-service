package com.creditya.authservice.api;

import com.creditya.authservice.api.mapper.UserMapper;
import com.creditya.authservice.usecase.authenticateuser.GetAllClientsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HandlerUser {
    private final UserMapper userMapper;
    private final GetAllClientsUseCase getAllClientsUseCase;

    public Mono<ServerResponse> getUsersByIds(ServerRequest request) {
        return request.bodyToMono(new ParameterizedTypeReference<List<UUID>>() {})
                .flatMapMany(userIds -> getAllClientsUseCase.getUsersByIds(userIds)
                        .map(userMapper::userResponseDTO))
                .collectList()
                .flatMap(users -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(users));
    }
}
