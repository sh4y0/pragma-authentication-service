package com.creditya.authservice.model.user.gateways;

import com.creditya.authservice.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepositoryGateway {
    Mono<User> findByEmail(String email);
    Mono<User> signUp(User user);
}
