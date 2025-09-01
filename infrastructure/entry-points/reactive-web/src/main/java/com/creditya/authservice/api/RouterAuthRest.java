package com.creditya.authservice.api;

import com.creditya.authservice.api.dto.request.UserLoginRequestDTO;
import com.creditya.authservice.api.dto.request.UserSignUpRequestDTO;
import com.creditya.authservice.api.dto.response.TokenDTO;
import com.creditya.authservice.api.exception.GlobalExceptionFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterAuthRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    method = RequestMethod.POST,
                    beanClass = HandlerAuth.class,
                    beanMethod = "signUpDoc",
                    operation = @Operation(
                            summary = "Register a new user",
                            description = "Creates a new user account. Requires ADMIN or ADVISER role.",
                            security = @SecurityRequirement(name = "bearerAuth"),
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserSignUpRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "User created successfully"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Validation Errors",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Unauthorized - Invalid or missing JWT token",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "Forbidden - Insufficient privileges (requires ADMIN or ADVISER role)",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "User Already Exists",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    beanClass = HandlerAuth.class,
                    beanMethod = "signInDoc",
                    operation = @Operation(
                            summary = "Login user",
                            description = "Authenticates a user and returns JWT tokens",
                            security = {}, // No security required for login
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserLoginRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login successful - Returns JWT access and refresh tokens",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(
                                                            implementation = TokenDTO.class,
                                                            example =  """
                                                                        {
                                                                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                                          "tokenType": "Bearer",
                                                                          "expiresIn": 3600
                                                                        }
                                                                        """
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Validation Errors",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Invalid credentials",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "500",
                                            description = "Internal server error",
                                            content = @Content(
                                                    mediaType = "application/json"
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(HandlerAuth handlerAuth, GlobalExceptionFilter globalExceptionFilter) {
        return route(POST("/api/v1/users"), handlerAuth::signUp)
                .and(route(POST("/api/v1/login"), handlerAuth::logIn))
                .filter(globalExceptionFilter);
    }
}