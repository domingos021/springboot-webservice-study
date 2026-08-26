package com.diniz.springbootstudy.services.security;

import com.diniz.springbootstudy.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// ============================================================================
// SECURITY USER DETAILS SERVICE
// ============================================================================
// Implements Spring Security's UserDetailsService interface.
// Acts as a bridge between Spring Security authentication engine and UserRepository.
// ============================================================================

@Service
public class AuthorizationService implements UserDetailsService {

    private final UserRepository repository;

    /**
     * Dependency injection via constructor.
     */
    public AuthorizationService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Locates the user based on the username (email).
     * Called automatically by Spring Security during authentication.
     *
     * Execution Flow:
     * HTTP Login Request ──> AuthenticationManager ──> loadUserByUsername ──> UserRepository
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /*
         * Queries the database for UserDetails using the provided email.
         * Unpacks Optional<User> (User entity implements UserDetails interface)
         * or throws UsernameNotFoundException if the email does not exist in DB.
         */
        return repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}