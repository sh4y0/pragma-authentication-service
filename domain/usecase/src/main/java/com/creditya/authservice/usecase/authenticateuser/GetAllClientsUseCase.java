package com.creditya.authservice.usecase.authenticateuser;

import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class GetAllClientsUseCase {
    private final UserRepositoryGateway userRepositoryGateway;
    private final RoleRepositoryGateway roleRepositoryGateway;
    private final UseCaseLogger useCaseLogger;

    public Flux<User> getUsersByIds(List<UUID> userIds) {
        useCaseLogger.trace("Fetching users by IDs: {}", userIds.size());
        return userRepositoryGateway.getUsersByIds(userIds)
                .switchIfEmpty(Flux.defer(() -> {
                    useCaseLogger.trace("No users found for given IDs");
                    return Flux.empty();
                }));
    }
}


