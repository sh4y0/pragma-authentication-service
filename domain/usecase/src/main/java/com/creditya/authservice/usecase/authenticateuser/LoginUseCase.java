package com.creditya.authservice.usecase.authenticateuser;

import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.UserToken;
import com.creditya.authservice.model.user.gateways.PasswordHasherGateway;
import com.creditya.authservice.model.user.gateways.TokenGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.usecase.authenticateuser.exception.InvalidCredentialsException;
import com.creditya.authservice.usecase.authenticateuser.exception.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {
    private final UserRepositoryGateway userRepositoryGateway;
    private final RoleRepositoryGateway roleRepository;
    private final TokenGateway tokenGateway;
    private final PasswordHasherGateway passwordHasherGateway;
    private final UseCaseLogger useCaseLogger;

    public Mono<UserToken> authenticate(String email, String password) {
        useCaseLogger.trace("Starting authentication for email: " + email);

        return userRepositoryGateway.findByEmail(email)
                .switchIfEmpty(Mono.defer(() -> {
                    useCaseLogger.trace("User not found: " + email);
                    return Mono.error(new InvalidCredentialsException());
                }))
                .flatMap(user -> validatePassword(user, password))
                .switchIfEmpty(Mono.defer(() -> {
                    useCaseLogger.trace("Invalid password for user: " + email);
                    return Mono.error(new InvalidCredentialsException());
                }))
                .flatMap(user ->
                        roleRepository.findByRoleId(user.getRoleId())
                                .switchIfEmpty(Mono.defer(() -> {
                                    useCaseLogger.trace("Role not found");
                                    return Mono.error(new RoleNotFoundException());
                                }))
                                .map(role -> {
                                    UserToken token = new UserToken(tokenGateway.generateToken(user, role));
                                    useCaseLogger.trace("Token successfully generated for user: " + email);
                                    return token;
                                })
                );
    }

    private Mono<User> validatePassword(User user, String rawPassword) {
        boolean matches = passwordHasherGateway.matches(rawPassword, user.getPassword());
        if (matches) {
            useCaseLogger.trace("Password valid for user: " + user.getEmail());
            return Mono.just(user);
        } else {
            useCaseLogger.trace("Password invalid for user: " + user.getEmail());
            return Mono.error(new InvalidCredentialsException());
        }
    }
}
