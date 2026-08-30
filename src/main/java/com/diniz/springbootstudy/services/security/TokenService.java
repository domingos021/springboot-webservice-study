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
     * JWT SECRET KEY FLOW:
     *
     * The secret key belongs to the Spring Boot application, not to individual users.
     *
     * Application (Spring Boot)
     *        |
     *        ▼
     * Configuration property:
     * api.security.token.secret
     *        |
     *        ▼
     * Secret value
     *        |
     *        ├── Uses the value defined in application.properties
     *        │
     *        └── If the property does not exist,
     *            Spring uses the default value below.
     *
     * The same secret key is used to:
     * 1. Sign JWT tokens during login.
     * 2. Verify JWT tokens received in future requests.
     *
     * Important:
     * - The secret belongs to the application, not to individual users.
     * - All users share the same secret key.
     * - Users are identified inside the JWT by claims
     *   (for example, the email stored in the subject claim).
     */

    /*
     * Injects the JWT secret from application.properties.
     * If the property is not found, Spring uses the default value.
     */
    @Value("${api.security.token.secret:my-secret-key-study-api-2026}")
    private String secret;


    // ========================================================================
    // TOKEN GENERATION
    // ========================================================================

    /**
     * Generates a signed JWT token containing the user's email as subject
     * and a 2-hour expiration time.
     * <p>
     * The generated JWT is composed of three parts:
     * <p>
     * HEADER
     * {
     * "alg": "HS256",
     * "typ": "JWT"
     * }
     * <p>
     * - Defines the algorithm used to sign the token.
     * - In this case, HMAC SHA-256.
     * <p>
     * <p>
     * PAYLOAD
     * {
     * "iss": "springboot-webservice-study",
     * "sub": "email@email.com",
     * "exp": "expiration timestamp"
     * }
     * <p>
     * - iss (issuer): identifies the application that generated the token.
     * - sub (subject): identifies the authenticated user.
     * In this implementation, the user's email is used.
     * - exp (expiration): defines when the token becomes invalid.
     * <p>
     * <p>
     * SIGNATURE
     * <p>
     * HMACSHA256(
     * header + payload,
     * secret
     * )
     * <p>
     * - Ensures the token was created by this application.
     * - Prevents anyone from modifying the token data without the secret key.
     *
     * @param user Authenticated user entity.
     * @return Encoded JWT token string.
     */
    //ENCODING TOKEN
// Here the user is used to get the email that represent the userName
    public String generateToken(User user) {
        try {

            // CREATE ALGORITHM
            // Creates the HMAC256 algorithm using the application's secret key.
            // This algorithm will later be used to sign the JWT.
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // BUILD + ENCODE + SIGN THE JWT
            // JWT.create() starts building the token.
            // The header and payload are internally encoded using Base64URL.
            return JWT.create()

                    // ADD CLAIM (ISSUER)
                    // Stores the application identifier inside the payload.
                    .withIssuer("springboot-webservice-study")

                    // ADD CLAIM (SUBJECT)
                    // Stores the authenticated user's email.
                    .withSubject(user.getEmail())

                    // ADD CLAIM (EXPIRATION)
                    // Stores the expiration timestamp.
                    .withExpiresAt(genExpirationDate())

                    // SIGN TOKEN
                    // Generates the cryptographic signature using HMAC256.
                    // After signing, the library assembles:
                    // Header + Payload + Signature
                    // and returns the encoded JWT string.
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


/*
 * JWT AUTHENTICATION FLOW:
 *
 * 1) USER LOGIN
 *
 * User sends:
 * {
 *   "email": "Carlos@yahoo.com",
 *   "password": "Senha123!"
 * }
 *
 *        |
 *        ▼
 *
 * 2) AuthenticationController
 *
 *        |
 *        ▼
 *
 * 3) AuthenticationManager
 *
 * Validates:
 * - user exists
 * - password matches BCrypt hash
 *
 *        |
 *        ▼
 *
 * 4) TokenService.generateToken(User user)
 *
 *        |
 *        ▼
 *
 * JWT.create()
 *        |
 *        ├── withIssuer()
 *        │       -> identifies the application that created the token
 *        │
 *        ├── withSubject()
 *        │       -> stores the authenticated user's identifier
 *        │          (here: user.getEmail())
 *        │
 *        ├── withExpiresAt()
 *        │       -> defines token expiration time
 *        │
 *        └── sign(algorithm)
 *                -> signs the token using the secret key
 *
 *        |
 *        ▼
 *
 * LoginResponseDTO
 *
 * {
 *    "token": "eyJhbGciOiJIUzI1Ni..."
 * }
 */

/*
JwtAuthenticationFilter
          |
          ▼
    TokenService
          |
          ├── generateToken()
          │       → cria o JWT no login
          │
          └── validateToken()
                  → verifica o JWT nas próximas requisições
 */

/*
LEITURA DE TOKEN
O site que você vai usar direto é este:
link:JWT Debugger (jwt.io)

Ele é o mais conhecido para JWT. Você cola o token completo e ele mostra:

HEADER
PAYLOAD
SIGNATURE (assinatura)

Exemplo do que você vai ver:
 */