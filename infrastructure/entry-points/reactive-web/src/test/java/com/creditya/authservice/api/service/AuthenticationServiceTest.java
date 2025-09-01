package com.creditya.authservice.api.service;


import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.usecase.authenticateuser.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    private final UserRepositoryGateway userRepo = Mockito.mock(UserRepositoryGateway.class);
    private final RoleRepositoryGateway roleRepo = Mockito.mock(RoleRepositoryGateway.class);

    private final AuthenticationService service =
            new AuthenticationService(userRepo, roleRepo);

    @Test
    void findByUsername_returnsUserDetails() {
        var user = new User();
        user.setEmail("t@t.com");
        user.setPassword("$2a$10$xxxx");
        user.setRoleId(UUID.randomUUID());

        when(userRepo.findByEmail("t@t.com")).thenReturn(Mono.just(user));
        var role = new Role();
        role.setRoleId(user.getRoleId());
        role.setName("ADMIN");
        when(roleRepo.findByRoleId(user.getRoleId())).thenReturn(Mono.just(role));

        StepVerifier.create(service.findByUsername("t@t.com"))
                .expectNextMatches(ud ->
                        ud.getUsername().equals("t@t.com")
                                && ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))
                )
                .verifyComplete();

        verify(userRepo).findByEmail("t@t.com");
        verify(roleRepo).findByRoleId(user.getRoleId());
    }

    @Test
    void findByUsername_throwsWhenEmpty() {
        when(userRepo.findByEmail("x@x.com")).thenReturn(Mono.empty());

        StepVerifier.create(service.findByUsername("x@x.com"))
                .expectError(InvalidCredentialsException.class)
                .verify();

        verify(userRepo).findByEmail("x@x.com");
        verifyNoMoreInteractions(roleRepo);
    }
}
