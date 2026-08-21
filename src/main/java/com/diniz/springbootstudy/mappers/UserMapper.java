package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.dto.UserInsertDTO;
import com.diniz.springbootstudy.dto.UserUpdateDTO;
import com.diniz.springbootstudy.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// ============================================================================
// MAPPER LAYER - ENTITY <-> DTO CONVERSION UTILITY
// ============================================================================
// Core Purpose:
// Centralizes all mapping logic between User entities and DTOs.
// Keeping this logic out of the Service layer ensures Single Responsibility (SRP).
//
// Advantages:
// - Decouples entity structure from external API contracts.
// - Handles sensitive business operations during mapping (e.g., password encryption).
// - Simplifies Service unit testing by allowing independent mapper testing.
// ============================================================================

/**
 * Spring Component responsible for converting User Entities to DTOs and vice-versa.
 */
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Converts a User Entity into a clean UserDTO for HTTP responses.
     * Strips out sensitive fields (like password) implicitly by omission.
     *
     * @param entity The source User entity from the database.
     * @return Filtered UserDTO ready for JSON response serialization.
     */
    public UserDTO toDTO(User entity) {
        if (entity == null) {
            return null;
        }
        return new UserDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone()
        );
    }

    /**
     * Converts a UserInsertDTO into a brand new User Entity for database insertion.
     * Automatically handles BCrypt password hashing before saving.
     *
     * @param dto Input creation request payload containing raw password.
     * @return Unpersisted User entity with hashed password.
     */
    public User toEntity(UserInsertDTO dto) {
        if (dto == null) {
            return null;
        }
        User entity = new User();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        // Hashing raw password into an irreversible BCrypt hash
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return entity;
    }

    /**
     * Updates an existing managed User Entity with data from UserUpdateDTO.
     * Note: Password field is intentionally ignored during standard update flow.
     *
     * @param dto Input update request payload.
     * @param entity Target JPA User entity retrieved from database.
     */
    public void updateEntityFromDTO(UserUpdateDTO dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
    }
}