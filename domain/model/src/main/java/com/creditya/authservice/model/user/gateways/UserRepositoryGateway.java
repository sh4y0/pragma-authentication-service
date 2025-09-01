package com.creditya.authservice.model.user.gateways;

import com.creditya.authservice.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserRepositoryGateway {
    Flux<User> getUsersByIds(List<UUID> userIds);
    Mono<User> findByEmail(String email);
    Mono<User> signUp(User user);

}
