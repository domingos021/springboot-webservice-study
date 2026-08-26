package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ============================================================================
// RESET PASSWORD REQUEST DTO
// ============================================================================
// Payload sent by client to set a new password using the received recovery token.
// ============================================================================

public record ResetPasswordDTO(
        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String newPassword
) {}