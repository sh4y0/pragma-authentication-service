package com.creditya.authservice.r2dbc;

import com.creditya.authservice.model.user.User;
import com.creditya.authservice.r2dbc.entity.UserEntity;
import com.creditya.authservice.r2dbc.user_adapter.UserReactiveRepository;
import com.creditya.authservice.r2dbc.user_adapter.UserReactiveRepositoryGatewayAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserReactiveRepositoryGatewayAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private final UserEntity userEntity = UserEntity.builder()
            .userId(UUID.randomUUID())
            .name("Jhon")
            .lastName("Doe")
            .email("jhon@gmail.com")
            .password("jhondoe")
            .dni("12345678")
            .phone("12345678")
            .roleId(UUID.randomUUID())
            .baseSalary(new BigDecimal(1000))
            .build();

    private final User user = User.builder()
            .userId(UUID.randomUUID())
            .name("Jhon")
            .lastName("Doe")
            .email("jhon@gmail.com")
            .password("jhondoe")
            .dni("12345678")
            .phone("12345678")
            .roleId(UUID.randomUUID())
            .baseSalary(new BigDecimal(1000))
            .build();

    @Test
    void mustFindValueById() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        when(repository.findById("1")).thenReturn(Mono.just(userEntity));

        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNextMatches(t -> t.getUserId().equals("1") && t.getName().equals("Jhon"))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    /*@Test
    void mustFindByExample() {
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just("test"));
        when(mapper.map("test", Object.class)).thenReturn("test");

        Flux<Object> result = repositoryAdapter.findByExample("test");

        StepVerifier.create(result)
                .expectNextMatches(value -> value.equals("test"))
                .verifyComplete();
    }*/

    @Test
    void mustSaveValue() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<Object> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
