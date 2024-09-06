package com.benabbou.microservices.user.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtService {

    @Value("${SECRET.value}")
    private String secret;

    /**
     * Validates the JWT token.
     *
     * @param token the JWT token to validate
     */
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }

    /**
     * Generates a JWT token for the given username and email.
     *
     * @param userName the username to include in the JWT
     * @param userEmail the email to include in the JWT
     * @return the generated JWT token
     */
    public String generateToken(String userName, String userEmail) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userEmail);  // Add email to the token claims
        return createToken(claims, userName);
    }

    /**
     * Creates the JWT token with claims and username as the subject.
     *
     * @param claims   the claims to include in the JWT
     * @param userName the username to include as the subject
     * @return the generated JWT token
     */
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)  // Username as the subject
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))  // 30-minute expiration
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Returns the signing key based on the secret value.
     *
     * @return the signing key
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
