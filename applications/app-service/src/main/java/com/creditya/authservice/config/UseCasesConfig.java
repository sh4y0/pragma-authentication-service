package com.creditya.authservice.config;

import com.creditya.authservice.model.utils.gateways.TransactionalGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.gateways.*;
import com.creditya.authservice.usecase.authenticateuser.LoginUseCase;
import com.creditya.authservice.usecase.authenticateuser.SignUpUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "com.creditya.authservice.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public SignUpUseCase signUpUseCase(
            UserRepositoryGateway userRepositoryGateway,
            RoleRepositoryGateway roleRepository,
            PasswordHasherGateway passwordHasher,
            TransactionalGateway transactionalGateway,
            UseCaseLogger useCaseLogger
    ) {
        return new SignUpUseCase(userRepositoryGateway, roleRepository, passwordHasher, transactionalGateway, useCaseLogger);
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepositoryGateway userRepositoryGateway,
            RoleRepositoryGateway roleRepository,
            TokenGateway tokenGateway,
            PasswordHasherGateway passwordHasherGateway,
            UseCaseLogger useCaseLogger
            //AuthenticationGateway authenticationGateway
    ) {
        return new LoginUseCase(userRepositoryGateway,
                                roleRepository,tokenGateway,
                                passwordHasherGateway,
                                useCaseLogger);
    }

}
