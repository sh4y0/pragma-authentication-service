package com.creditya.authservice.config;

import com.creditya.authservice.model.role.gateways.RoleRepositoryGateway;
import com.creditya.authservice.model.user.gateways.PasswordHasherGateway;
import com.creditya.authservice.model.user.gateways.TokenGateway;
import com.creditya.authservice.model.user.gateways.UserRepositoryGateway;
import com.creditya.authservice.model.utils.gateways.TransactionalGateway;
import com.creditya.authservice.model.utils.gateways.UseCaseLogger;
import com.creditya.authservice.usecase.authenticateuser.LoginUseCase;
import com.creditya.authservice.usecase.authenticateuser.SignUpUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();
            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }
            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
        }
    }


    @Test
    @DisplayName("Should register SignUpUseCase bean in application context")
    void testSignUpUseCaseBeanExists() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            SignUpUseCase signUpUseCase = context.getBean(SignUpUseCase.class);
            assertNotNull(signUpUseCase, "SignUpUseCase bean should be registered");
        }
    }

    @Test
    @DisplayName("Should register SignInUseCase bean in application context")
    void testSignInUseCaseBeanExists() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            LoginUseCase signInUseCase = context.getBean(LoginUseCase.class);
            assertNotNull(signInUseCase, "LoginUseCase bean should be registered");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UserRepositoryGateway userRepositoryGateway() { return mock(UserRepositoryGateway.class); }
        @Bean
        public RoleRepositoryGateway roleRepository() { return mock(RoleRepositoryGateway.class); }
        @Bean
        public PasswordHasherGateway passwordHasher() { return mock(PasswordHasherGateway.class); }
        @Bean
        public TransactionalGateway transactionalGateway() { return mock(TransactionalGateway.class); }
        @Bean
        public UseCaseLogger useCaseLogger() { return mock(UseCaseLogger.class); }
        @Bean
        public TokenGateway tokenGateway() { return mock(TokenGateway.class); }
    }
}