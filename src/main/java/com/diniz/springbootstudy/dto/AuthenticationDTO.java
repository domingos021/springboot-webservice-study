package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ============================================================================
// AUTHENTICATION REQUEST DTO
// ============================================================================
// Data Transfer Object that encapsulates the client login credentials.
// Received via HTTP POST /auth/login payload.
// ============================================================================

public record AuthenticationDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {}