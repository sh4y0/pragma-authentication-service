package com.creditya.authservice.model.user;

import lombok.Builder;

@Builder
public record UserToken(String token) {
}
