package com.diniz.springbootstudy.dto;

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

    private String name;
    private String email;
    private String phone;
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
    "name": "Maria",
    "email": "maria@gmail.com",
    "phone": "999999999",
    "password": "123"
  }
         │
         ▼
  [ UserInsertDTO ] ──► Contains: name, email, phone, password (raw)
         │
         ▼
    UserService     ──► 1. Criptografa a senha via passwordEncoder.encode("123")
         │              2. Salva no banco e gera o ID = 2
         ▼
    [ UserDTO ]     ──► Contains: id (2), name, email, phone (senha descarta!)
         │
         ▼
  HTTP Response (201 Created)
  {
    "id": 2,
    "name": "Maria",
    "email": "maria@gmail.com",
    "phone": "999999999"
  }
 ============================================================================
*/