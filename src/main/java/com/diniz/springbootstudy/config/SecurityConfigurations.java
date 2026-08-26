package com.diniz.springbootstudy.config;

import com.diniz.springbootstudy.services.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ============================================================================
// SPRING SECURITY CONFIGURATION CLASS
// ============================================================================
// Central security configuration bean defining HTTP filters, route permissions,
// session management policies, and password encoders.
// ============================================================================

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Dependency injection via constructor.
     */
    public SecurityConfigurations(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the HTTP security filter chain.
     *
     * Execution Flow:
     * Disable CSRF ──> Set STATELESS Session ──> Define Authorization Rules ──> Add JWT Filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Release H2 Console access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // ===================================================
                        // PUBLIC ENDPOINTS (No authentication required)
                        // ===================================================
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll() // Public product showcase
                        .requestMatchers("/h2-console/**").permitAll() // Access to H2 Database Web Console

                        // ===================================================
                        // ADMIN RESTRICTED ENDPOINTS (Requires ROLE_ADMIN)
                        // ===================================================
                        .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")

                        // ===================================================
                        // PRIVATE ENDPOINTS (Requires valid Bearer JWT token)
                        // ===================================================
                        .requestMatchers(HttpMethod.POST, "/orders/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()

                        // Any other request must be authenticated
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Exposes the AuthenticationManager bean used by AuthenticationController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


/*
  Client (Postman / Frontend)
           │
           │ 1. HTTP Request + Bearer JWT Token
           ▼
 JwtAuthenticationFilter ──> Validates Token & Sets SecurityContextHolder
           │
           ▼
    UserController       ──> Validates JSON DTOs (@Valid)
           │
           ▼
     UserService         ──> Executes business rules & transactions
           │
           ▼
     UserMapper          ──> Encrypts passwords (BCrypt) & converts DTO <-> Entity
           │
           ▼
    UserRepository       ──> Handles SQL persistence via Spring Data JPA
           │
           ▼
       Database
 */