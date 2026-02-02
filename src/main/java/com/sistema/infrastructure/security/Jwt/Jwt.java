package com.sistema.infrastructure.security.Jwt;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.infrastructure.config.AccessToken;
import com.sistema.infrastructure.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Jwt {

    private final SecretKeyGenerator secretKeyGenerator;

    @Value("${jwt.expiration-minutes:15}")
    private long expirationMinutes;

    @Value("${jwt.issuer:sistema-biblioteca-api}")
    private String issuer;

    @Value("${jwt.audience:frontend-oficial}")
    private String audience;

    private Date getExpiration() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(expirationMinutes);
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Retorna o tempo de expiração do token em segundos.
     */
    public long getExpirationInSeconds() {
        return expirationMinutes * 60;
    }

    private Map<String, Object> generateTokenClaims(Funcionarios funcionario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", funcionario.getNome());
        return claims;
    }

    public AccessToken getAccessToken(Funcionarios funcionario) {
        SecretKey secretKey = secretKeyGenerator.getSecretKey();
        Date expiration = getExpiration();

        String token = Jwts.builder()
                .setIssuer(issuer)
                .setAudience(audience)
                .setSubject(funcionario.getLogin())
                .setExpiration(expiration)
                .setIssuedAt(new Date())
                .addClaims(generateTokenClaims(funcionario))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();

        return new AccessToken(token);
    }

    public String getLoginFromToken(String token) {
        try {
            SecretKey secretKey = secretKeyGenerator.getSecretKey();

            var jws = Jwts.parserBuilder()
                    .requireIssuer(issuer)
                    .requireAudience(audience)
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            Claims body = jws.getBody();
            return body.getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }
    }
}
