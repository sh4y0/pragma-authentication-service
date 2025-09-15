package com.creditya.authservice.usecase.authenticateuser;

import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class GetClientByIdUseCase {

    private final UserRepositoryGateway userRepositoryGateway;
    private final UseCaseLogger useCaseLogger;

    public Mono<User> getUserById(UUID userId) {
        useCaseLogger.trace("Fetching user by ID: {}", userId);
        return userRepositoryGateway.getUsersById(userId)
                .switchIfEmpty(Mono.defer(() -> {
                    useCaseLogger.trace("No user found for given ID");
                    return Mono.empty();
                }));
    }
}
