package com.diniz.springbootstudy.services.email;

// ============================================================================
// EMAIL SERVICE CONTRACT
// ============================================================================
// Abstraction layer for sending outbound system communications.
// Decouples application logic from specific email delivery vendors or protocols.
// ============================================================================

public interface EmailService {

    /**
     * Dispatches a password recovery email containing the reset token link.
     *
     * @param recipientEmail Destination email address.
     * @param token          Generated UUID reset token.
     */
    void sendPasswordResetEmail(String recipientEmail, String token);
}