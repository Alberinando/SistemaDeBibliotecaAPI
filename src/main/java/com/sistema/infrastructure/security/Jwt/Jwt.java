package com.sistema.infrastructure.security.Jwt;

import com.sistema.domain.entities.Funcionarios;
import com.sistema.infrastructure.config.AccessToken;
import com.sistema.infrastructure.exceptions.InvalidTokenException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
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

    private Date getExpiration() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(60);
        return Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
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
                .setSubject(funcionario.getLogin())
                .setExpiration(expiration)
                .addClaims(generateTokenClaims(funcionario))
                .signWith(secretKey)
                .compact();
        return new AccessToken(token);
    }

    public String getLoginFromToken(String token) {
        try{
            SecretKey secretKey = secretKeyGenerator.getSecretKey();

            var jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return jws.getBody().getSubject();
        } catch (JwtException e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
}
