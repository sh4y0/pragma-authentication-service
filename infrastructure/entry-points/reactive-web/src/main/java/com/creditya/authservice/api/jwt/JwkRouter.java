package com.creditya.authservice.api.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class JwkRouter {
    @Bean
    public RouterFunction<ServerResponse> jwkSetRoutes(JwkHandler jwkHandler) {
        return RouterFunctions
                .route(GET("/.well-known/jwks.json"), jwkHandler::getJwkSet);
    }
}
