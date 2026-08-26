package com.diniz.springbootstudy.mappers;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.dto.UserInsertDTO;
import com.diniz.springbootstudy.dto.UserUpdateDTO;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.entities.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

// ============================================================================
// MAPPER LAYER - ENTITY <-> DTO CONVERSION UTILITY
// ============================================================================
// Core Purpose:
// Centralizes all conversion logic between the persistence layer (Entity)
// and the API contract layer (DTOs).
//
// Why this class exists:
// Instead of spreading mapping logic throughout the Service layer,
// this component encapsulates every transformation in a single place,
// improving maintainability, readability and testability.
//
// Responsibilities:
// ✔ Convert Entity -> DTO for API responses.
// ✔ Convert Insert DTO -> Entity for database insertion.
// ✔ Copy Update DTO data into an existing managed Entity.
// ✔ Encrypt raw passwords before persistence.
// ✔ Assign default user roles during creation.
//
// Non-Responsibilities:
// ✘ Persist entities.
// ✘ Execute business rules.
// ✘ Access repositories.
//
// Benefits:
// - Single Responsibility Principle (SRP)
// - Cleaner Service layer
// - Better separation of concerns
// - Centralized password hashing
// - Easier unit testing
//
// Overall Mapping Flow:
//
//              HTTP Request
//                    │
//                    ▼
//            UserInsertDTO
//                    │
//                    ▼
//              UserMapper
//                    │
//                    ├── BCrypt Password Hashing & Role Assignment
//                    ▼
//               User Entity
//                    │
//                    ▼
//              UserRepository
//                    │
//                    ▼
//                Database
//
//              HTTP Response
//                    ▲
//                    │
//               UserDTO
//                    ▲
//                    │
//              UserMapper
//                    ▲
//                    │
//               User Entity
// ============================================================================

/**
 * Spring Component responsible for mapping User entities and DTOs.
 * <p>
 * This mapper is intentionally stateless. Every conversion is deterministic
 * and depends only on the provided input objects.
 */
@Component
public class UserMapper {

    /**
     * Password hashing strategy provided by the Spring IoC container.
     * <p>
     * Registered in SecurityConfig as a BCryptPasswordEncoder bean.
     * Used only when converting creation requests into persistent entities.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the mapper with all required dependencies.
     * <p>
     * Spring automatically injects the PasswordEncoder bean defined
     * inside SecurityConfig.
     *
     * @param passwordEncoder BCrypt encoder used to hash raw passwords.
     */
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // ------------------------------------------------------------------------
    // ENTITY -> DTO
    // ------------------------------------------------------------------------

    /**
     * ARCHITECTURE FLOW:
     *
     * <pre>
     * [Database]
     *      │
     *  (Entity: User)   ──> Allows modifying data in DB and running business logic
     *      │
     * [Service Layer]   ──> Executes logic and calls the mapper: mapper.toDTO(user)
     *      │
     *  (DTO: UserDTO)   ──> Passive data carrier (data only), safe to travel network
     *      │
     * [Controller / API]
     *      │
     * [Client / Front-end]
     * </pre>
     * <p>
     * Converts a {@link User} entity into its {@link UserDTO} representation.
     * Sensitive information (such as the password hash) is intentionally excluded.
     *
     * <pre>
     * User Entity  ----------------->  UserDTO
     *       │                         │
     *       └------ mapper.toDTO() ---┘
     * </pre>
     *
     * @param entity Source entity retrieved from the database.
     * @return A new instance of {@link UserDTO} with exposed fields, or null if entity is null.
     */

    // ============================================================================
    // METHOD NAMING CONVENTION
    // ============================================================================
    // "to" means "convert to another representation".
    //
    // "DTO" represents a Data Transfer Object, which is an object used to transfer
    // data between application layers (for example, Entity -> API Response).
    //
    // Therefore:
    // toDTO = "convert the current object into a DTO representation"
    //
    // The method does not create a new DTO type.
    // It creates a new instance of the existing UserDTO class,
    // copying only the fields that should be exposed.
    // ============================================================================

    // ============================================================================
    // THE MAPPER AS A "FILTERED COPY FACTORY"
    // ============================================================================
    // Analogy:
    //
    // • User (Database Entity): A heavy physical folder containing 20 documents
    //   (including sensitive contracts, passwords, and full history).
    //
    // • UserDTO (Class): The blueprint/template of a simplified form.
    //
    // • toDTO() (Method): The process of using "new UserDTO()" to instantiate
    //   a real, clean object in memory containing only the allowed fields to
    //   hand over to the requester.
    // ============================================================================

    // ------------------------------------------------------------------------
    // ENTITY -> DTO CONVERSION IMPLEMENTATIONS
    // ------------------------------------------------------------------------

    // APPROACH 1: Functional implementation using Java Optional & Lambda expression (PREFERRED)
    // Pros: Fluent pipeline, declarative style, aligns with Spring Data JPA functional paradigms.
    public UserDTO toDTO(User entity) {
        // Optional handles the null check (replacing traditional 'if'), verifying if the entity is present.
        return Optional.ofNullable(entity)
                // LAMBDA EXPRESSION
                // If present, maps the entity by instantiating a new UserDTO and passing entity fields as constructor arguments.
                .map(e -> new UserDTO(e.getId(), e.getName(), e.getEmail(), e.getPhone()))
                // If the entity is null, returns null.
                .orElse(null);
    }

    /*
    // APPROACH 2: Traditional Imperative implementation using Guard Clause (IF)
    // Pros: Direct control flow, micro-optimization (avoids Optional object allocation).
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
    */

    // ------------------------------------------------------------------------
    // INSERT DTO -> ENTITY
    // ------------------------------------------------------------------------
    //
    // Purpose:
    // Creates a brand-new User entity from an insertion request.
    //
    // Extra Responsibilities:
    // 1. Encrypt raw password via BCrypt.
    // 2. Assign default UserRole.CLIENT authority level.
    //
    // Flow:
    //
    //      UserInsertDTO
    //            │
    //            ▼
    //      passwordEncoder.encode() + setRole(CLIENT)
    //            │
    //            ▼
    //        User Entity
    //
    // ------------------------------------------------------------------------

    // ==========================Insert =============================================

    /**
     * ARCHITECTURE FLOW (INSERT / CREATION):
     *
     * <pre>
     * [Client / Front-end]
     *      │
     * [Controller / API] ──> Receives raw data (UserInsertDTO with plain text password)
     *      │
     * [Service Layer]   ──> Calls mapper: mapper.toEntity(dto)
     *      │
     *  (Entity: User)   ──> Holds hashed password, default UserRole, and business state
     *      │
     *  [Database]       ──> Persists secure record
     * </pre>
     * <p>
     * Converts a {@link UserInsertDTO} into a new {@link User} entity.
     *
     * <pre>
     * UserInsertDTO  ----------------->  User Entity
     *       │                                │
     *       └------ mapper.toEntity() -------┘
     * </pre>
     * <p>
     * The password received from the client is NEVER stored in plain text.
     * Before assigning it to the entity, it is transformed into an irreversible
     * BCrypt hash. Assigns default UserRole.CLIENT if none specified.
     *
     * @param dto Incoming creation request.
     * @return New User entity ready for persistence, or null if dto is null.
     */
    public User toEntity(UserInsertDTO dto) {
        // Optional handles the null check (replacing traditional 'if'), verifying if the incoming DTO is present.
        return Optional.ofNullable(dto)
                // If present, maps the DTO into a new User entity, populating fields and hashing the raw password via BCrypt.
                .map(d -> {
                    /*
                     * Here we instantiate a new User entity object and populate its fields
                     * using the pre-validated data received from the UserInsertDTO.
                     */
                    User entity = new User();

                    // Fields already validated by Bean Validation inside UserInsertDTO
                    entity.setName(d.getName());
                    entity.setEmail(d.getEmail());
                    entity.setPhone(d.getPhone());

                    /*
                     * UserInsertDTO receives the raw plain-text password from the client.
                     * The Mapper intercepts it, passes it through the PasswordEncoder bean,
                     * and assigns the resulting secure BCrypt hash to the entity.
                     */
                    entity.setPassword(passwordEncoder.encode(d.getPassword()));

                    /*
                     * Assigns default authority level (CLIENT) for new user registrations.
                     */
                    entity.setRole(UserRole.CLIENT);

                    // Finally, returns the fully populated, secure entity ready for database persistence.
                    return entity;
                })
                // If the DTO is null, returns null.
                .orElse(null);
    }

    // ------------------------------------------------------------------------
    // UPDATE DTO -> EXISTING ENTITY
    // ------------------------------------------------------------------------
    //
    // Purpose:
    // Copies editable fields from the Update DTO into an already managed
    // JPA entity.
    //
    // Password is intentionally ignored.
    //
    // Future password changes should be handled by a dedicated use case,
    // such as:
    //
    //      changePassword(...)
    //
    // ------------------------------------------------------------------------

    /**
     * ARCHITECTURE FLOW (UPDATE):
     *
     * <pre>
     * [Client / Front-end]
     *      │
     * [Controller / API] ──> Sends UserUpdateDTO with modified fields
     *      │
     * [Service Layer]   ──> Loads existing User from DB (JPA Managed Entity)
     *      │                Calls mapper.updateEntityFromDTO(dto, entity)
     *      │
     *  (Entity: User)   ──> Receives updated values in memory
     *      │                JPA Dirty Checking automatically pushes UPDATE to DB
     *      │
     *  [Database]       ──> Updated record persisted
     * </pre>
     * <p>
     * Copies editable fields from a {@link UserUpdateDTO} into an existing {@link User} entity.
     *
     * <pre>
     * UserUpdateDTO + Existing User Entity  ----->  Modified User Entity
     *                       │                               │
     *                       └-- updateEntityFromDTO() ------┘
     * </pre>
     * <p>
     * The target entity is expected to be managed by the JPA Persistence Context.
     * Password updates are intentionally excluded from this operation.
     *
     * @param dto    Source update request containing new values.
     * @param entity Existing managed entity to be modified in place.
     */
    public void updateEntityFromDTO(UserUpdateDTO dto, User entity) {

        // GUARD CLAUSE: Early exit if either the DTO or target Entity is null (prevents NullPointerException)
        if (dto == null || entity == null) {
            return;
        }

        /*
         * We copy the pre-validated fields from the UserUpdateDTO directly into the existing User entity.
         * Since this entity is currently managed by the JPA Persistence Context (loaded from DB in the Service layer),
         * JPA Dirty Checking will automatically detect these field changes and flush the UPDATE query to the DB.
         *
         * Mechanics:
         * If both the entity and DTO exist, we invoke the setter on the entity's specific field (e.g., entity.setName(...))
         * and pass the new validated value from the UserUpdateDTO (e.g., dto.getName()).
         *
         * Result:
         * The field value inside the managed entity is updated in memory (e.g., changing from "John" to "Mary")
         * and will be persisted upon transaction commit.
         */
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        // Notice: Password is intentionally NOT updated here for security reasons.
    }
}