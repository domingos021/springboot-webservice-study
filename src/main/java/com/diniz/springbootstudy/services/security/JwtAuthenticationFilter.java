package com.diniz.springbootstudy.services.security;

import com.diniz.springbootstudy.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ============================================================================
// JWT AUTHENTICATION FILTER
// ============================================================================
// Intercepts every HTTP request once to extract, validate, and set the JWT user
// context into Spring Security's SecurityContextHolder.
// ============================================================================

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // using two classes as attributes
    private final TokenService tokenService;// generates the taken,validate
    private final UserRepository userRepository;

    /**
     * Dependency injection via constructor.
     */
    public JwtAuthenticationFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    /**
     * Internal filter logic executed on every request.
     *
     * Execution Flow:
     * HTTP Request ──> Extract Header ──> Validate Token ──> Set Security Context ──> Continue Filter Chain
     */
    @Override
    @NullMarked
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Recovers the Bearer token from the Authorization header.
         */
        String token = recoverToken(request); // after the generation of the token

        if (token != null) {
            /*
             * Extracts the subject (email) from the token if valid.
             */
            String email = tokenService.validateToken(token); // if token exists, applies to the token the function that validate the token

            //if email not empty
            if (!email.isEmpty()) {
                /*
                 * Safely inspects the Optional<User> returned by findByEmail(email).
                 * If present, converts User entity (which implements UserDetails) into authentication context.
                 */
                // The 'user' is the lambda parameter (->).
                // Its value is provided by the Optional<User>
                // returned by findByEmail(email), if a user is found.
                // the method findByEmail is defined in the userRepository, tha expects an email
                userRepository.findByEmail(email).ifPresent(user -> {
                    /*
                     * Creates the authentication object and sets it inside Spring Security context.
                     *
                     * user.getAuthorities()
                     * => returns the authorities (roles/permissions) assigned to this user.
                     * It does not verify the user role here; it only provides the user's granted authorities
                     * so Spring Security can use them later for authorization checks.
                     */
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );
                    // Saves the authenticated user information in the SecurityContext.
                    // Spring Security uses this context to identify the current logged-in user
                    // during the request lifecycle.
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }

        /*
         * Proceeds with the execution chain (next filters or target Controller).
         */
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to extract Bearer token from HTTP Authorization header.
     */
    private String recoverToken(HttpServletRequest request) {

        // Gets the value of the HTTP Authorization header from the request.
        // Example:
        // Authorization: Bearer eyJhbGciOiJIUzI1Ni...
        String authHeader = request.getHeader("Authorization");


        // Checks if the Authorization header is missing or
        // does not follow the expected Bearer token format.
        // If invalid, there is no JWT token to recover.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }


        // Removes the "Bearer " prefix and returns only the JWT token.
        //
        // Before:
        // Bearer eyJhbGciOiJIUzI1Ni...
        //
        // After:
        // eyJhbGciOiJIUzI1Ni...
        //
        // This extracted token will be sent to TokenService
        // for validation and decoding.
        return authHeader.replace("Bearer ", "");
    }
}