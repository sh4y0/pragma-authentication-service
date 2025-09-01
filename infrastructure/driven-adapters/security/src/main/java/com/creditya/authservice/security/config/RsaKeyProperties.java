package com.creditya.authservice.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "rsa.key")
public record RsaKeyProperties(Resource privateKey, Resource publicKey) {
}
