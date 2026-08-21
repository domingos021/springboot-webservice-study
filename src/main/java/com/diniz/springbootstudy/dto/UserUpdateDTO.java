package com.diniz.springbootstudy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - USER UPDATE INPUT CONTRACT
// ============================================================================
// Core Purpose:
// Represents the incoming request body payload for updating existing users (PUT /users/{id}).
//
// Key Differences from UserInsertDTO:
// - Excludes 'password': Password updates must be handled via a dedicated security endpoint.
// - Excludes 'id': The target user ID is provided via URL path variable (@PathVariable).
// ============================================================================

/**
 * Data Transfer Object for receiving user update data payloads (HTTP PUT).
 */
public class UserUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9]+$",
            message = "Phone must contain only numbers"
    )
    private String phone;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public UserUpdateDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param name  User Name
     * @param email User Email
     * @param phone User Phone
     */
    public UserUpdateDTO(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
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
}