package com.creditya.authservice.usecase.authenticateuser;

import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.PasswordHasherGateway;
import com.creditya.authservice.model.utils.gateways.TransactionalGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.exception.RoleNotFoundException;
import com.creditya.authservice.usecase.authenticateuser.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import static com.creditya.authservice.usecase.authenticateuser.utils.ROLE.CUSTOMER;

@RequiredArgsConstructor
public class SignUpUseCase {
    private final UserRepositoryGateway userRepositoryGateway;
    private final RoleRepositoryGateway roleRepository;
    private final PasswordHasherGateway passwordHasher;
    private final TransactionalGateway transactionalGateway;
    private final UseCaseLogger useCaseLogger;

    public Mono<User> signUp(User user) {
        useCaseLogger.trace("Starting sign-up for email: " + user.getEmail());

        return transactionalGateway.executeInTransaction(
                userRepositoryGateway.findByEmail(user.getEmail())
                        .hasElement()
                        .flatMap(userExists -> {
                            if (Boolean.TRUE.equals(userExists)) {
                                useCaseLogger.trace("User already exists: " + user.getEmail());
                                return Mono.error(new UserAlreadyExistsException());
                            }

                            useCaseLogger.trace("User does not exist, checking CUSTOMER role...");

                            return roleRepository.findByName(CUSTOMER.toString())
                                    .switchIfEmpty(Mono.defer(() -> {
                                        useCaseLogger.trace("Role CUSTOMER not found for sign-up");
                                        return Mono.error(new RoleNotFoundException());
                                    }))
                                    .flatMap(role -> {
                                        user.setPassword(passwordHasher.hash(user.getPassword()));
                                        user.setRoleId(role.getRoleId());
                                        useCaseLogger.trace("Signing up user: " + user.getEmail());
                                        return userRepositoryGateway.signUp(user);
                                    });
                        })
        );
    }
}
