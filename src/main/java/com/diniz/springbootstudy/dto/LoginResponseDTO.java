package com.diniz.springbootstudy.dto;

// ============================================================================
// LOGIN RESPONSE DTO
// ============================================================================
// Data Transfer Object returned upon successful authentication.
// Contains the generated Bearer JWT token.
// ============================================================================

public record LoginResponseDTO(String token) {}