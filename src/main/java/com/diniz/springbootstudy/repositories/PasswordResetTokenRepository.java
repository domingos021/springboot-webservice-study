package com.diniz.springbootstudy.repositories;

import com.diniz.springbootstudy.entities.PasswordResetToken;
import com.diniz.springbootstudy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// ============================================================================
// PASSWORD RESET TOKEN REPOSITORY
// ============================================================================
// Provides database queries for PasswordResetToken management.
// ============================================================================

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /*
     * Retrieves token entity by its UUID string representation.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /*
     * Retrieves active token for a specific user if exists.
     */
    Optional<PasswordResetToken> findByUser(User user);
}