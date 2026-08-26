package com.diniz.springbootstudy.services.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// ============================================================================
// MOCK EMAIL SERVICE IMPLEMENTATION
// ============================================================================
// Simulates email dispatching by outputting message details to server logs.
// Ideal for local development and testing environments without SMTP setup.
// ============================================================================

@Service
public class MockEmailService implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendPasswordResetEmail(String recipientEmail, String token) {
        LOG.info("==========================================================================");
        LOG.info("SIMULATING EMAIL DISPATCH TO: {}", recipientEmail);
        LOG.info("SUBJECT: Password Reset Request");
        LOG.info("BODY: Hello! Use the following token to reset your password:");
        LOG.info("TOKEN: {}", token);
        LOG.info("EXPIRES IN: 30 minutes");
        LOG.info("==========================================================================");
    }
}