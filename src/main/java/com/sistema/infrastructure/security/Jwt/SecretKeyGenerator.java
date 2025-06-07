package com.sistema.infrastructure.security.Jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class SecretKeyGenerator {
    private SecretKey secretKey;

    public SecretKey getSecretKey() {
        if (secretKey == null) {
            secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        return secretKey;
    }
}
