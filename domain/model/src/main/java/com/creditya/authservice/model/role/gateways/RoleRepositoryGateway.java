package com.creditya.authservice.model.role.gateways;

import com.creditya.authservice.model.role.Role;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RoleRepositoryGateway {

    Mono<Role> findByName(String roleName);
    Mono<Role> findByRoleId(UUID id);
}
