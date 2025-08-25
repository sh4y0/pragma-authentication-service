package com.creditya.authservice.api.service;

import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.usecase.authenticateuser.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AuthenticationService implements ReactiveUserDetailsService{
    private final UserRepositoryGateway userRepositoryGateway;
    private final RoleRepositoryGateway roleRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepositoryGateway.findByEmail(email)
                .flatMap(user ->
                        roleRepository.findByRoleId(user.getRoleId())
                                .map(role -> org.springframework.security.core.userdetails.User
                                        .withUsername(user.getEmail())
                                        .password(user.getPassword())
                                        .authorities(role.getName())
                                        .build()
                                )
                )
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()));
    }
}
