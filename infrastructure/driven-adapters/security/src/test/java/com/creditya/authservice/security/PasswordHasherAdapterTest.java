package com.creditya.authservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherAdapterTest {

    @Test
    void hash_and_matches() {
        var adapter = new PasswordHasherAdapter(new BCryptPasswordEncoder());

        String raw = "secret123";
        String hashed = adapter.hash(raw);

        assertNotEquals(raw, hashed);
        assertTrue(adapter.matches(raw, hashed));
        assertFalse(adapter.matches("wrong", hashed));
    }
}
