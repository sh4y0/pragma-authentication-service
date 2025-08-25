package com.creditya.authservice.r2dbc;

import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.r2dbc.entity.RoleEntity;
import com.creditya.authservice.r2dbc.role_adapter.RoleReactiveRepository;
import com.creditya.authservice.r2dbc.role_adapter.RoleReactiveRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleReactiveRepositoryAdapterTest {

    @InjectMocks
    private RoleReactiveRepositoryAdapter roleAdapter;

    @Mock
    private RoleReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private RoleEntity sampleRoleEntity;
    private Role sampleRole;
    private final UUID roleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Arrange: Create common sample objects for tests
        sampleRoleEntity = RoleEntity.builder()
                .roleId(roleId)
                .name("CUSTOMER")
                .description("Customer role")
                .build();

        sampleRole = Role.builder()
                .roleId(roleId)
                .name("CUSTOMER")
                .description("Customer role")
                .build();
    }

    @Test
    @DisplayName("findByName should return a Role when it exists")
    void findByName_whenRoleExists_shouldReturnRole() {
        // Arrange: Mock the repository to return a role entity
        when(repository.findByName("CUSTOMER")).thenReturn(Mono.just(sampleRoleEntity));
        // Arrange: Mock the mapper to convert the entity to a domain object
        when(mapper.map(sampleRoleEntity, Role.class)).thenReturn(sampleRole);

        // Act: Call the adapter method
        Mono<Role> result = roleAdapter.findByName("CUSTOMER");

        // Assert: Use StepVerifier to validate the reactive stream
        StepVerifier.create(result)
                .expectNextMatches(role -> {
                    assert role.getRoleId().equals(roleId);
                    assert role.getName().equals("CUSTOMER");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("findByName should return Mono.empty when role does not exist")
    void findByName_whenRoleDoesNotExist_shouldReturnEmpty() {
        // Arrange: Mock the repository to return an empty Mono
        when(repository.findByName(anyString())).thenReturn(Mono.empty());

        // Act: Call the adapter method
        Mono<Role> result = roleAdapter.findByName("NON_EXISTENT_ROLE");

        // Assert: Verify that the stream completes without emitting any items
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByRoleId should return a Role when it exists")
    void findByRoleId_whenRoleExists_shouldReturnRole() {
        // Arrange: Mock the repository to return a role entity by its ID
        when(repository.findById(roleId)).thenReturn(Mono.just(sampleRoleEntity));
        // Arrange: Mock the mapper
        when(mapper.map(sampleRoleEntity, Role.class)).thenReturn(sampleRole);

        // Act: Call the adapter method
        Mono<Role> result = roleAdapter.findByRoleId(roleId);

        // Assert: Validate the emitted role
        StepVerifier.create(result)
                .expectNext(sampleRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("findByRoleId should return Mono.empty when role does not exist")
    void findByRoleId_whenRoleDoesNotExist_shouldReturnEmpty() {
        // Arrange: Mock the repository to return an empty Mono for any UUID
        when(repository.findById(any(UUID.class))).thenReturn(Mono.empty());

        // Act: Call the adapter method with a non-existent ID
        Mono<Role> result = roleAdapter.findByRoleId(UUID.randomUUID());

        // Assert: Verify that the stream is empty
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }
}