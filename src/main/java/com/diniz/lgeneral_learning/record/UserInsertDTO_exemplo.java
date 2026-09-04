package com.diniz.lgeneral_learning.record;

public class UserInsertDTO_exemplo {

    /*
     * ============================================================================
     * DATA TRANSFER OBJECT (DTO) USING RECORD EXAMPLE
     * ============================================================================
     *
     * A record is useful for DTOs because it only carries data.
     *
     * In this example, UserInsertDTO receives the data sent by the client when
     * creating a new user (POST /users).
     *
     * The record automatically creates:
     *
     * - private final attributes
     * - constructor
     * - accessor methods (name(), email(), phone(), password())
     * - equals()
     * - hashCode()
     * - toString()
     *
     * This avoids writing repetitive code such as getters, setters and constructors.
     *
     * ============================================================================
     *
     * package com.diniz.springbootstudy.dto;
     *
     * import jakarta.validation.constraints.Email;
     * import jakarta.validation.constraints.NotBlank;
     * import jakarta.validation.constraints.Pattern;
     * import jakarta.validation.constraints.Size;
     *
     *
     * // ============================================================================
     * // USER CREATION INPUT CONTRACT
     * // ============================================================================
     *
     * // Purpose:
     * // Represents the incoming request body payload for creating a new user.
     *
     * // Differences from UserDTO:
     * // - Does not contain the ID because the database generates it automatically.
     * // - Contains the raw password before encryption by PasswordEncoder.
     *
     *
     * public record UserInsertDTO(
     *
     *     @NotBlank(message = "Name is required")
     *     @Size(
     *          min = 3,
     *          max = 80,
     *          message = "Name must be between 3 and 80 characters"
     *     )
     *     String name,
     *
     *
     *     @NotBlank(message = "Email is required")
     *     @Email(message = "Please enter a valid email address")
     *     String email,
     *
     *
     *     @NotBlank(message = "Phone is required")
     *     @Pattern(
     *          regexp = "^[0-9]+$",
     *          message = "Phone must contain only numbers"
     *     )
     *     String phone,
     *
     *
     *     // PASSWORD VALIDATION POLICY:
     *     //
     *     // @NotBlank:
     *     // Password cannot be null, empty or only whitespace.
     *     //
     *     // @Size:
     *     // Password length must be between 8 and 20 characters.
     *     //
     *     // @Pattern:
     *     // Requires:
     *     // - at least one digit
     *     // - at least one lowercase letter
     *     // - at least one uppercase letter
     *     // - at least one special character
     *
     *
     *     @NotBlank(message = "Password is required")
     *     @Size(
     *          min = 8,
     *          max = 20,
     *          message = "Password must be between 8 and 20 characters"
     *     )
     *     @Pattern(
     *          regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
     *          message = "Password must contain at least one digit, one lowercase, one uppercase letter, and one special character"
     *     )
     *     String password
     *
     * ) {}
     *
     *
     * ============================================================================
     * RECORD ACCESS EXAMPLE
     * ============================================================================
     *
     * // Before using record:
     * dto.getEmail();
     * dto.getPassword();
     *
     *
     * // With record:
     * dto.email();
     * dto.password();
     *
     *
     * ============================================================================
     * IMPORTANT:
     * ============================================================================
     *
     * Records are recommended for DTOs because they transport data only.
     *
     * They are not recommended for JPA Entities (@Entity), because entities need:
     *
     * - mutable state
     * - JPA lifecycle management
     * - Hibernate proxy support
     * - relationships between entities
     *
     * Example:
     *
     * @Entity
     * public class User {
     * }
     *
     * ============================================================================
     */
}