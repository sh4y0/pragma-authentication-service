package com.creditya.authservice.api.jwt;

import com.creditya.authservice.security.jwt.JwtAdapter;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwkHandler {

    private final JwtAdapter jwtAdapter;

    public Mono<ServerResponse> getJwkSet(ServerRequest request) {
        return Mono.fromCallable(() -> {
            JWKSet jwkSet = new JWKSet(jwtAdapter.getJwk());
            return jwkSet.toJSONObject();
        }).flatMap(jwkSetMap ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(jwkSetMap)
        );
    }
}
