package com.creditya.authservice.r2dbc.role_adapter;

import com.creditya.authservice.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

// TODO: This file is just an example, you should delete or modify it
public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, UUID>, ReactiveQueryByExampleExecutor<RoleEntity> {
    Mono<RoleEntity> findByName(String name);
}
