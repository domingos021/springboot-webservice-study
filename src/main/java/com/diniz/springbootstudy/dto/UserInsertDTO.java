package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - USER CREATION INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for creating new users (POST /users).
//
// Key Differences from UserDTO:
// - Excludes 'id': The ID does not exist prior to database insertion (auto-generated).
// - Includes 'password': Captures the raw password sent by the client so that
//   the UserService can encrypt it via PasswordEncoder before persisting.
// ============================================================================

/**
 * Data Transfer Object for receiving user creation data payloads (HTTP POST).
 */
public class UserInsertDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    /*
     * PASSWORD VALIDATION POLICY:
     * - @NotBlank: Password field cannot be null, empty, or whitespace.
     * - @Size: Must be between 8 and 20 characters long.
     * - @Pattern (Strong Password Regex):
     *     (?=.*[0-9])       -> At least one digit (0-9)
     *     (?=.*[a-z])       -> At least one lowercase letter (a-z)
     *     (?=.*[A-Z])       -> At least one uppercase letter (A-Z)
     *     (?=.*[@#$%^&+=!]) -> At least one special character
     *
     * EXAMPLES OF ACCEPTED PASSWORDS:
     * - "Senha123!"
     * - "Admin2026@"
     * - "Diniz#85"
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
    )
    private String password;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public UserInsertDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param name User Name
     * @param email User Email
     * @param phone User Phone
     * @param password Raw User Password
     */
    public UserInsertDTO(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/*
 ============================================================================
 DTO INPUT VS. OUTPUT DUALITY IN POST REQUEST
 ============================================================================

  HTTP POST Request Body (JSON)
  {
    "name": "Maria Silva",
    "email": "maria@gmail.com",
    "phone": "61984615326",
    "password": "Senha123!"  ◄── Exemplo de senha válida aceita pelo filtro
  }
         │
         ▼
  [ UserInsertDTO ] ──► Contains: name, email, phone, password (raw)
         │
         ▼
    UserService     ──► 1. Criptografa a senha via passwordEncoder.encode("Senha123!")
         │              2. Salva no banco e gera o ID = 2
         ▼
    [ UserDTO ]     ──► Contains: id (2), name, email, phone (senha descarta!)
         │
         ▼
  HTTP Response (201 Created)
  {
    "id": 2,
    "name": "Maria Silva",
    "email": "maria@gmail.com",
    "phone": "61984615326"
  }
 ============================================================================
*/