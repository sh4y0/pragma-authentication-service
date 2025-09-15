package com.creditya.authservice.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterUserRest {
    @Bean
    public RouterFunction<ServerResponse> routerUserFunction(HandlerUser handlerUser) {
        return route(POST("/api/v1/clients/by-ids"), handlerUser::getUsersByIds)
                .and(route(GET("/api/v1/clients/{userId}"), handlerUser::getUserById));
    }

}
