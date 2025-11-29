package com.sistema.infrastructure.security.Jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class SecretKeyGenerator {
    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Getter
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("Variável de ambiente 'JWT_SECRET' não configurada.");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (Exception ex) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }
}
