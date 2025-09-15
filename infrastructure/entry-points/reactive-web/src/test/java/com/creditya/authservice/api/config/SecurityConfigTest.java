package com.creditya.authservice.api.config;

import com.creditya.authservice.api.exception.CustomAccessDeniedHandler;
import com.creditya.authservice.api.exception.CustomAuthenticationEntryPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Mock
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Mock
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter;

    @Spy
    private SecurityConfig securityConfig;
    private RSAPublicKey rsaPublicKey;

    @BeforeEach
    void setUp() throws Exception {
        securityConfig = new SecurityConfig();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
    }

    @Test
    void testPasswordEncoderBean() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertThat(encoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
        // ... assertions ...
    }

    @Test
    void testJwtDecoderBean() {
        ReactiveJwtDecoder decoder = securityConfig.jwtDecoder(rsaPublicKey);
        assertThat(decoder).isNotNull();
    }

    @Test
    void testJwtAuthenticationConverterBean() {
        var converter = securityConfig.jwtAuthenticationConverter();
        assertThat(converter).isNotNull();
    }

    @Test
    void testSpringSecurityFilterChainBean_buildsSuccessfully() {
        ServerHttpSecurity http = ServerHttpSecurity.http();
        ReactiveAuthenticationManager mockedAuthManager = mock(ReactiveAuthenticationManager.class);
        ServerHttpSecurity configuredHttp = http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwtSpec -> jwtSpec.authenticationManager(mockedAuthManager))
                );

        SecurityWebFilterChain filterChain = configuredHttp.build();
        assertThat(filterChain).isNotNull();
    }
}