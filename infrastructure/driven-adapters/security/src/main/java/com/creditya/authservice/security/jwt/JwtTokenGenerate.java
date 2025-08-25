package com.creditya.authservice.security.jwt;


import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.TokenGateway;
import com.creditya.authservice.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerate implements TokenGateway {

    private final JwtProvider jwtProvider;

    @Override
    public String generateToken(User user, Role role) {
        return jwtProvider.generateToken(
                org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities(new SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .build()
        );
    }
}