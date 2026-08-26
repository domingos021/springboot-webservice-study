package com.diniz.springbootstudy.services.security;

import com.diniz.springbootstudy.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final TokenService tokenService;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        /*
         * Recovers the Bearer token from the Authorization header.
         */
        String token = recoverToken(request);

        if (token != null) {
            /*
             * Extracts the subject (email) from the token if valid.
             */
            String email = tokenService.validateToken(token);

            if (!email.isEmpty()) {
                /*
                 * Safely inspects the Optional<User> returned by findByEmail(email).
                 * If present, converts User entity (which implements UserDetails) into authentication context.
                 */
                userRepository.findByEmail(email).ifPresent(user -> {
                    /*
                     * Creates the authentication object and sets it inside Spring Security context.
                     */
                    var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
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
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}