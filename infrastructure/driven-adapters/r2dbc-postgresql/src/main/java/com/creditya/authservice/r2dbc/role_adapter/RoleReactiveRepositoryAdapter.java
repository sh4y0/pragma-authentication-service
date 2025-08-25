package com.creditya.authservice.r2dbc.role_adapter;

import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.r2dbc.entity.RoleEntity;
import com.creditya.authservice.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<Role, RoleEntity, UUID, RoleReactiveRepository> implements RoleRepositoryGateway {
    public RoleReactiveRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }


    @Override
    public Mono<Role> findByName(String roleName) {
        return this.repository.findByName(roleName).map(this::toEntity);
    }

    @Override
    public Mono<Role> findByRoleId(UUID id) {
        return this.repository.findById(id).map(this::toEntity);
    }
}
