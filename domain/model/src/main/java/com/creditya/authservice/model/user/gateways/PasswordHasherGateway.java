package com.creditya.authservice.model.user.gateways;

public interface PasswordHasherGateway {
    String hash(String password);
    boolean matches(String rawPassword, String hashedPassword);
}
