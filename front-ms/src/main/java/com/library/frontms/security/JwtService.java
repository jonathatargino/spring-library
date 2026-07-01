package com.library.frontms.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtService {

    private final SecretKey signingKey;
    private final Duration expiracao;

    public JwtService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-minutes}") long expiracaoMinutos) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracao = Duration.ofMinutes(expiracaoMinutos);
    }

    public String gerarToken(String username, String role) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracao)))
                .signWith(signingKey)
                .compact();
    }
}
