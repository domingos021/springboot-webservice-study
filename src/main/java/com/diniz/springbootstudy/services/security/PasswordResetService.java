package com.diniz.springbootstudy.services.security;

import com.diniz.springbootstudy.dto.ForgotPasswordDTO;
import com.diniz.springbootstudy.dto.ResetPasswordDTO;
import com.diniz.springbootstudy.entities.PasswordResetToken;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.repositories.PasswordResetTokenRepository;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.email.EmailService;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

// ============================================================================
// PASSWORD RESET SERVICE
// ============================================================================
// Coordinates end-to-end password recovery workflows:
// 1. Generation and persistence of secure 30-minute UUID reset tokens.
// 2. Triggering email notifications.
// 3. Token validation and password update with BCrypt hashing.
// ============================================================================

@Service
public class PasswordResetService {

    private static final long EXPIRATION_MINUTES = 30;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Dependency injection via constructor.
     */
    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    // ========================================================================
    // WORKFLOW 1: REQUEST PASSWORD RESET
    // ========================================================================

    /**
     * Generates a temporary reset token and dispatches the recovery email.
     * If an existing token exists for the user, it is replaced.
     * this method receives a ForgotPasswordDTO containing the user's email address from the client request through.
     * controller
     */
    @Transactional
    public void createPasswordResetToken(ForgotPasswordDTO dto) {

        /*
         * 1. Query database for existing user by email(searches the email in the dto on database)
         * Throws ResourceNotFoundException if user is not found.
         */
        User user = userRepository.findUserByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.email()));

        /*
         * 2. Remove any previously generated active token for this user to avoid dangling tokens.
         */
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        /*
         * 3. Generate a new secure random UUID token and set 30-minute expiration.
         */
        String tokenValue = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);

        PasswordResetToken resetToken = new PasswordResetToken(null, tokenValue, user, expiryDate);

        /*
         * 4. Persist token to database.
         */
        tokenRepository.save(resetToken);

        /*
         * 5. Dispatch notification email containing the token.
         */
        emailService.sendPasswordResetEmail(user.getEmail(), tokenValue);
    }

    // ========================================================================
    // WORKFLOW 2: EXECUTE PASSWORD RESET
    // ========================================================================

    /**
     * Validates the provided token and updates the user password with BCrypt.
     */
    @Transactional
    public void resetPassword(ResetPasswordDTO dto) {

        /*
         * 1. Find token entity by token string value.
         */
        PasswordResetToken resetToken = tokenRepository.findByToken(dto.token())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or non-existent password reset token."));

        /*
         * 2. Check if token has expired.
         */
        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Password reset token has expired. Please request a new one.");
        }

        /*
         * 3. Retrieve linked User entity, encode new password via BCrypt, and persist.
         */
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);

        /*
         * 4. Delete token after successful password reset (one-time use policy).
         */
        tokenRepository.delete(resetToken);
    }
}