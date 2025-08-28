package com.creditya.authservice.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {
    private final JwtProvider jwtProvider;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);
        String username;

        try {
            username = jwtProvider.extractUsername(jwt);
        } catch (Exception e) {
            log.error("Error extrayendo el nombre de usuario del JWT: {}", e.getMessage());
            return chain.filter(exchange);
        }

        if (username == null) {
            return chain.filter(exchange);
        }

        return userDetailsService.findByUsername(username)
                .flatMap(userDetails -> {
                    boolean isValid = jwtProvider.isTokenValid(jwt, userDetails);

                    if (!isValid) {
                        log.debug("JWT no válido");
                        return chain.filter(exchange);
                    }

                    List<GrantedAuthority> authorities = jwtProvider.extractClaim(jwt, claims -> {
                        List<String> roles = ((List<?>) claims.get("roles"))
                                .stream()
                                .map(Object::toString)
                                .toList();

                        return roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    });

                    UsernamePasswordAuthenticationToken auth =
                            new  UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    SecurityContextImpl context = new SecurityContextImpl(auth);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                });
    }
}