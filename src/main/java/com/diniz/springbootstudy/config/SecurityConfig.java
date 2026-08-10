package com.diniz.springbootstudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// ============================================================================
// SECURITY CONFIGURATION LAYER - PASSWORD ENCODER BEAN
// ============================================================================
// Purpose:
// Exposes the PasswordEncoder bean to the Spring Context.
// Allows injecting PasswordEncoder into UserService without needing a full
// authentication/security chain.
// ============================================================================

@Configuration
public class SecurityConfig {

    /**
     * Registers BCryptPasswordEncoder as a Spring Bean.
     * BCrypt is the industry standard for password hashing with built-in salt generation.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}