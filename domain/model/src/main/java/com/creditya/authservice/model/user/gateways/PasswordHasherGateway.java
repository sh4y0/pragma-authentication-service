package com.creditya.authservice.model.user.gateways;

public interface PasswordHasherGateway {
    //String encode(String rawPassword);
    String hash(String password);
    boolean matches(String rawPassword, String hashedPassword);
}
