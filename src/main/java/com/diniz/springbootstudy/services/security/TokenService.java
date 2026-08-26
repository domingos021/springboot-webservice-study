package com.diniz.springbootstudy.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.diniz.springbootstudy.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

// ============================================================================
// JWT TOKEN SERVICE
// ============================================================================
// Service responsible for encoding, decoding, signing, and validating JWTs
// (JSON Web Tokens) using HMAC256 cryptographic algorithms.
// ============================================================================

@Service
public class TokenService {

    /*
     * Secret key injected from application.properties (or default secret).
     */
    @Value("${api.security.token.secret:my-secret-key-study-api-2026}")
    private String secret;

    // ========================================================================
    // TOKEN GENERATION
    // ========================================================================

    /**
     * Generates a signed JWT token containing the user's email as subject
     * and a 2-hour expiration window.
     *
     * @param user Authenticated user entity.
     * @return Encoded JWT Bearer String.
     */
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withIssuer("springboot-webservice-study")
                    .withSubject(user.getEmail())
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating JWT token", exception);
        }
    }

    // ========================================================================
    // TOKEN VALIDATION
    // ========================================================================

    /**
     * Validates the provided JWT string against signature and expiration.
     *
     * @param token Encoded JWT string received in HTTP Authorization header.
     * @return The user email (subject) if valid, or empty string if invalid.
     */
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("springboot-webservice-study")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    // ========================================================================
    // EXPIRATION UTILITY
    // ========================================================================

    /**
     * Generates an Instant representing 2 hours from current time in UTC-3 offset.
     */
    private Instant genExpirationDate() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}