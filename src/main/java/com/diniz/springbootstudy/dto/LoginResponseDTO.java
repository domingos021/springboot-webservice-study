package com.diniz.springbootstudy.dto;

// ============================================================================
// LOGIN RESPONSE DTO
// ============================================================================
// Data Transfer Object returned upon successful authentication.
// Contains the generated Bearer JWT token.
// ============================================================================

/*
 * A record is ideal for DTOs because it is designed to carry data only.
 *
 * Without a record, we would write:
 *
 * public class LoginResponseDTO {
 *
 *     private final String token;
 *
 *     public LoginResponseDTO(String token) {
 *         this.token = token;
 *     }
 *
 *     public String getToken() {
 *         return token;
 *     }
 * }
 *
 * By replacing "class" with "record":
 *
 * public record LoginResponseDTO(String token) {}
 *
 * the Java compiler automatically generates:
 *
 * - a private final field
 * - a constructor
 * - an accessor method: token()      // NOT getToken()
 * - equals()
 * - hashCode()
 * - toString()
 *
 * Therefore, the empty body {} is completely normal because the compiler
 * generates all the required boilerplate code.
 *
 * Records are commonly used for DTOs because they are immutable and only
 * transport data between application layers.
 */

public record LoginResponseDTO(String token) {}