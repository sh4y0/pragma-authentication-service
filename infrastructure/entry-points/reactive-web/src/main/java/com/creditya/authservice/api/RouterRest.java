package com.creditya.authservice.api;

import com.creditya.authservice.api.exception.GlobalExceptionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler, GlobalExceptionFilter globalExceptionFilter) {
        return route(POST("/api/v1/users"), handler::signUp)
                .and(route(POST("/api/v1/login"), handler::logIn)).filter(globalExceptionFilter);
    }
}
