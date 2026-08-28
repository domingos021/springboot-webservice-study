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

/**
 * Spring Security configuration bean responsible for defining HTTP authorization
 * boundaries, stateless session management, CSRF protection, and JWT filter chain placement.
 */
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
     * Configures the HTTP security filter chain and defines fine-grained access policies.
     *
     * Execution Flow:
     * Disable CSRF ──> Disable Frame Options (H2) ──> Set STATELESS Session ──> Define Authorization Rules ──> Add JWT Filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Release H2 Console access
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize

                        // ===================================================
                        // 1. PUBLIC ENDPOINTS (No authentication required)
                        // ===================================================
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_LOGIN).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_FORGOT_PASSWORD).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.AUTH_RESET_PASSWORD).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiRoutes.USERS_BASE).permitAll() // Public user self-registration
                        .requestMatchers(HttpMethod.GET, ApiRoutes.PRODUCTS_WILDCARD).permitAll() // Public product catalog showcase
                        .requestMatchers(HttpMethod.GET, ApiRoutes.CATEGORIES_WILDCARD).permitAll() // Public category catalog showcase
                        .requestMatchers(ApiRoutes.H2_CONSOLE).permitAll() // Access to H2 Database Web Console
                        .requestMatchers(ApiRoutes.TEST_RESET).permitAll() // Test environment database reset endpoint

                        // ===================================================
                        // 2. SPECIFIC PRIVATE ENDPOINTS (LOGGED USER PROFILE)
                        // CRITICAL: Evaluated before generic ADMIN boundaries to avoid path matching collision
                        // ===================================================
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_ME).authenticated()
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.USERS_ME).authenticated()
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE + "/me").authenticated() // User personal orders (/orders01/me)

                        // ===================================================
                        // 3. ADMIN RESTRICTED ENDPOINTS (Requires ROLE_ADMIN Authority)
                        // Real-world Rule: Full administrative control over users, catalogs & global orders
                        // ===================================================
                        .requestMatchers(ApiRoutes.ADMIN_BASE).hasAuthority("ROLE_ADMIN") // ProductAdminController (/admin/products)

                        // Product & Category Management Mutations (ADMIN Only)
                        .requestMatchers(HttpMethod.POST, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.PRODUCTS_WILDCARD).hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.POST, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.CATEGORIES_WILDCARD).hasAuthority("ROLE_ADMIN")

                        // User Administration (ADMIN Only)
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_BASE).hasAuthority("ROLE_ADMIN") // List all system users
                        .requestMatchers(HttpMethod.GET, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Find user by ID
                        .requestMatchers(HttpMethod.PUT, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Update user by ID
                        .requestMatchers(HttpMethod.DELETE, ApiRoutes.USERS_WILDCARD).hasAuthority("ROLE_ADMIN") // Delete user by ID

                        // Order System-Wide Listing (ADMIN Only)
                        // Restricts global order list so regular clients cannot inspect company sales
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE).hasAuthority("ROLE_ADMIN")

                        // ===================================================
                        // 4. PRIVATE ENDPOINTS (Requires valid Bearer JWT token)
                        // Allows logged clients to interact with specific order operations
                        // ===================================================
                        .requestMatchers(HttpMethod.GET, ApiRoutes.ORDERS_BASE + "/*").authenticated() // Detailed order view (/orders01/{id})
                        .requestMatchers(HttpMethod.POST, ApiRoutes.ORDERS_BASE).authenticated() // New order placement (/orders01)

                        // Any other unmapped request must be authenticated
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
 * CHAVE DE TESTE POSTMAN (VAULT)
 * badef917c56d1a3711a814389fdeeb39065e74981e06926569f35ee860944b2c
 */

/*
+-------------------+--------------------+--------------------+
| Recurso / Ação    | CLIENT (Cliente)   | ADMIN (Administrador)|
+-------------------+--------------------+--------------------+
| Ver Produtos      | ✅ Permitido       | ✅ Permitido       |
| Fazer Pedido      | ✅ Apenas o seu    | ✅ Permitido       |
| Ver Meus Pedidos  | ✅ (/orders01/me)  | ✅ Permitido       |
| Ver Todos Pedidos | ❌ 403 Forbidden   | ✅ (/orders01)     |
| Listar Usuários   | ❌ 403 Forbidden   | ✅ (/users)        |
| Deletar Usuários  | ❌ 403 Forbidden   | ✅ (/users/{id})   |
| Criar Produtos    | ❌ 403 Forbidden   | ✅ (/products)     |
+-------------------+--------------------+--------------------+
*/