package com.creditya.authservice.security.jwt;


import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.user.User;
import com.creditya.authservice.model.user.gateways.TokenGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAdapter implements TokenGateway {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    private static final String ROLES_CLAIM = "roles";

    @Getter
    private final String keyId = "creditya-key-id-2025-09";

    @Override
    public String generateToken(User user, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header()
                .keyId(this.keyId)
                .and()
                .issuer("creditya-auth-service")
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim(ROLES_CLAIM, List.of(role.getName()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(24, ChronoUnit.HOURS)))
                .signWith(this.privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public Claims validateTokenAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(this.publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get(ROLES_CLAIM);
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return List.of();
    }

    public JWK getJwk() {
        return new RSAKey.Builder(this.publicKey)
                .keyID(this.keyId)
                .build();
    }
}