package com.creditya.authservice.r2dbc.user_adapter;

import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.r2dbc.entity.UserEntity;
import com.creditya.authservice.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public class UserReactiveRepositoryGatewayAdapter extends ReactiveAdapterOperations<User, UserEntity, UUID, UserReactiveRepository
> implements UserRepositoryGateway {
    public UserReactiveRepositoryGatewayAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }


    @Override
    public Flux<User> getUsersByIds(List<UUID> userIds) {
        return this.repository.findAllById(userIds)
                .map(this::toEntity);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return  this.repository.findByEmail(email).map(this::toEntity);
    }

    @Override
    public Mono<User> signUp(User user) {
        return Mono.fromCallable(() -> this.toData(user))
                .flatMap(userEntity -> this.repository.save(userEntity))
                .map(saved -> user);
                //this.repository.save(this.toData(user)).map(this::toEntity);

    }
}
