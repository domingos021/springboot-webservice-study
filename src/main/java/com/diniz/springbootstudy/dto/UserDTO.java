package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - FIELD FILTER & CONTRACT DEFINITION
// ============================================================================
// Core Purpose:
// The DTO acts as an EXPLICIT FIELD FILTER over the JPA Entity (@Entity).
// You (the developer) decide exactly which fields are exposed and returned to
// the client in HTTP responses, filtering out anything internal or sensitive.
//
// Key Functions:
// - Custom Field Filtering: Selectively returns only client-facing attributes.
// - Security & Data Privacy: Omits sensitive fields (e.g., password) from JSON responses.
// - API Decoupling: Schema changes in database tables won't directly break the client contract.
// - Prevents Serialization Loops: Avoids infinite JSON recursion on JPA relationships (@OneToMany).
// ============================================================================

/**
 * Data Transfer Object representing the filtered, public User data payload for API responses.
 *
 * Filters the {@link User} entity to return only client-safe fields (id, name, email, phone),
 * intentionally excluding internal fields like 'password'.
 */
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public UserDTO() {
    }

    /**
     * Parameterized Constructor (Mainly for Unit Testing).
     *
     * Note: Not used in the standard production application flow (where 'new UserDTO(entity)' is preferred),
     * but essential for Unit Tests (JUnit 5 / Mockito) to instantiate mock DTO objects directly
     * without needing to build full JPA User entities.
     *
     * @param id User ID
     * @param name User Name
     * @param email User Email
     * @param phone User Phone
     */
    public UserDTO(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * Entity Conversion & Filtering Constructor (PRODUCTION USE).
     * Selectively maps only the desired fields from a JPA {@link User} entity into {@link UserDTO},
     * acting as a filter that strips out internal/sensitive attributes (such as 'password').
     *
     * @param entity The source User entity retrieved from the database.
     */
    public UserDTO(User entity) {
        this.id = entity.getId(); // fetching the ID from the entity user
        this.name = entity.getName(); // fetching the Name from the entity user
        this.email = entity.getEmail(); // fetching the Email from the entity user
        this.phone = entity.getPhone(); // fetching the Phone from the entity user
        // Notice: entity.getPassword() is intentionally NOT mapped here!
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

/*
 ============================================================================
 DTO AS A FIELD FILTERING PIPELINE
 ============================================================================

 Database Table (tb_user)
       │
       ▼
 [ User Entity ]  <-- Full Database Row:
       │              • id
       │              • name
       │              • email
       │              • phone
       │              • password  ◄── (Internal / Sensitive)
       │
       │ (Conversion via new UserDTO(entity) applies the field filter)
       ▼
   [ UserDTO ]    <-- Filtered Payload (Only what the developer chooses to expose):
       │              • id
       │              • name
       │              • email
       │              • phone
       │              [ password is STRIPPED OUT ]
       ▼
 UserController   <-- Returns UserDTO wrapped inside ResponseEntity<UserDTO>
       │
       ▼
 HTTP Client      <-- Receives clean, safe JSON without sensitive data
 ============================================================================
*/