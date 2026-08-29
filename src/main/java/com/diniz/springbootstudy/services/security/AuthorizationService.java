package com.diniz.springbootstudy.services.security;

import com.diniz.springbootstudy.repositories.UserRepository;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
// Provided by the Spring Security module.
// This class becomes available because the project includes:
// spring-boot-starter-security dependency in pom.xml.
//
// UserDetails is an interface that represents the authenticated user's data.
// The User entity implements this interface to integrate with Spring Security.

import org.springframework.security.core.userdetails.UserDetailsService;
// Provided by the Spring Security module.
// This class becomes available because the project includes:
// spring-boot-starter-security dependency in pom.xml.
//
// UserDetailsService defines the contract used by Spring Security
// to load user information during the authentication process.
// The AuthorizationService implements this interface.

import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Provided by the Spring Security module.
// This class becomes available because the project includes:
// spring-boot-starter-security dependency in pom.xml.
//
// Exception thrown when Spring Security cannot find a user
// during the authentication process.

import org.springframework.stereotype.Service;
// Provided by the Spring Framework module.
// This class becomes available through Spring Boot starter dependencies.
//
// @Service registers this class as a Spring Bean,
// allowing dependency injection and automatic management by Spring.


// ============================================================================
// DEPENDENCY ORIGIN
// ============================================================================
//
// These classes are NOT added individually in pom.xml.
//
// Maven dependencies provide libraries (JAR files), and those libraries contain
// multiple Java classes and interfaces.
//
// Example:
//
// pom.xml
//    │
//    ▼
// spring-boot-starter-security
//    │
//    ▼
// Spring Security JAR
//    │
//    ├── UserDetails
//    ├── UserDetailsService
//    └── UsernameNotFoundException
//
// Spring Boot manages the dependency versions automatically.
// ============================================================================

// ============================================================================
// SECURITY USER DETAILS SERVICE
// ============================================================================
// Implements Spring Security's UserDetailsService interface.
// Acts as a bridge between Spring Security authentication engine and UserRepository.
// ============================================================================

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;  // local repository object from user

    /**
     * Dependency injection via constructor.
     */
    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Locates the user based on the username identifier (email).
     * <p>
     * This method is automatically called by Spring Security during authentication.
     * <p>
     * Execution Flow:
     *
     * <pre>
     * HTTP Login Request
     *          |
     *          ▼
     * AuthenticationManager
     *          |
     *          ▼
     * AuthenticationProvider
     *          |
     *          ▼
     * UserDetailsService.loadUserByUsername()
     *          |
     *          ▼
     * UserRepository
     *          |
     *          ▼
     * User Entity (implements UserDetails)
     * </pre>
     */
    @Override // Overrides the loadUserByUsername() method from Spring Security's UserDetailsService interface
    @NonNull
    // Explicitly declares that this method never returns null because Spring Security uses a @NullMarked contract
    public UserDetails loadUserByUsername(@NonNull String username)
            throws UsernameNotFoundException {

        /*
         * Searches the database for a user using the provided email.
         *
         * The repository returns Optional<User>.
         *
         * The User entity implements UserDetails, therefore the returned User object
         * already contains the information required by Spring Security:
         * - username (email)
         * - encrypted password
         * - granted authorities (roles)
         *
         * If no user is found, UsernameNotFoundException is thrown,
         * causing authentication to fail.
         */
        return repository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + username
                        ));
    }
}


/*
 * Authentication Flow:
 *
 * This method is not called manually by the application code.
 * Spring Security automatically invokes it during the authentication process.
 *
 * The "username" parameter receives the identifier provided during login.
 * In this application, the username represents the user's email.
 *
 * Example:
 *
 * Login request:
 * {
 *     "email": "Maria@yahoo.com",
 *     "password": "Senha123!"
 * }
 *
 * Spring Security calls:
 *
 * loadUserByUsername("Maria@yahoo.com")
 *
 * Then this method uses the UserRepository to search the database:
 *
 * repository.findByEmail("Maria@yahoo.com")
 *
 * The returned User entity implements UserDetails, allowing Spring Security
 * to continue the authentication process by calling:
 *
 * - getUsername()    -> returns the email identifier
 * - getPassword()    -> returns the encrypted password
 * - getAuthorities() -> returns the user's roles/permissions
 *
 * Flow:
 *
 * User Login
 *      |
 *      ▼
 * AuthenticationManager
 *      |
 *      ▼
 * UserDetailsService
 *      |
 *      ▼
 * loadUserByUsername(email)
 *      |
 *      ▼
 * UserRepository
 *      |
 *      ▼
 * User Entity (implements UserDetails)
 *      |
 *      ▼
 * Spring Security validates credentials and permissions
 */
/*
@Override
@NonNull
public UserDetails loadUserByUsername(@NonNull String username)
        throws UsernameNotFoundException {

 */