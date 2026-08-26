package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ============================================================================
// FORGOT PASSWORD REQUEST DTO
// ============================================================================
// Payload sent by client requesting password recovery email.
// ============================================================================

public record ForgotPasswordDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}