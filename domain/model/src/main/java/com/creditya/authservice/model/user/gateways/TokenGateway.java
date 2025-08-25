package com.creditya.authservice.model.user.gateways;

import com.creditya.authservice.model.role.Role;
import com.creditya.authservice.model.user.User;

public interface TokenGateway {
    String generateToken(User user, Role role);
}
