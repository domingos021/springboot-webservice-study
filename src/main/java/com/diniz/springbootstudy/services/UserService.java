package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.dto.UserInsertDTO;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================================
// SERVICE LAYER ARCHITECTURE
// ============================================================================
// HTTP Request
//       │
//       ▼
// UserController (@RestController)
//       │
//       ▼
// UserService (@Service)   ◄── Current class
//       │
//       ▼
// UserRepository (@Repository)
//       │
//       ▼
// Database
//
// Responsibilities:
// - Contains business rules and application logic.
// - Receives requests/data from the Controller layer.
// - Encrypts sensitive data (passwords) using BCrypt before persisting.
// - Handles persistence exceptions and database integrity constraints.
// - Uses the Repository layer to communicate with the database.
// - Converts Entities to DTOs before returning data back to the Controller layer.
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 *
 * Service indicates that this class holds business logic.
 * Spring automatically manages its lifecycle, allowing it to be injected
 * into controllers or other services.
 */
@Service
public class UserService {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     // The Spring Framework automatically injects the dependency
     // directly into the field using reflection.
     //
     // @Autowired
     // private UserRepository repository;
    */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // This service depends on UserRepository and PasswordEncoder.
    // Spring automatically injects the beans into this constructor.
    //
    // Advantages:
    // - Explicit dependency contract.
    // - Fields can be marked as 'final' (immutability).
    // - Easy to mock in unit tests without Spring Context.
    // =========================================================
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        /*
         * Fetches User entities from the database and converts the list to UserDTO
         * using Java Streams and the UserDTO constructor reference.
         */
        List<User> list = repository.findAll();
        return list.stream().map(UserDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        /*
         * Handling Optional<User>:
         *
         * Instead of returning null (which yields an empty 200 OK), we throw a custom
         * ResourceNotFoundException when the ID is not present in the database.
         * The exception is caught globally by @ControllerAdvice to return a proper 404 Not Found.
         */
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        /*
         * Creates a new User entity from the input DTO (UserInsertDTO).
         * Encrypts the raw password using BCryptPasswordEncoder before database persistence.
         * Returns a clean UserDTO (without exposing the password hash in the response).
         */
        User entity = new User();
        entity.setName(dto.getName()); // Updates the entity name field with the incoming DTO data
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        // Hashing raw password into an irreversible BCrypt hash before saving
        String passwordHash = passwordEncoder.encode(dto.getPassword());
        entity.setPassword(passwordHash);

        entity = repository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        /*
         * Uses getReferenceById(id) instead of findById(id) to avoid an extra SELECT database query.
         * JPA prepares a monitored entity proxy and only executes the UPDATE query when transaction commits.
         *
         * Catches EntityNotFoundException when the specified ID does not exist in the database,
         * rethrowing a custom ResourceNotFoundException (HTTP 404).
         */
        try {
            User entity = repository.getReferenceById(id);
            updateData(entity, dto);
            entity = repository.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public void delete(Long id) {
        /*
         * Checks if the user exists before trying to delete.
         *
         * Catches DataIntegrityViolationException if the entity has foreign key ties
         * in other tables (e.g., linked Orders), rethrowing a custom DatabaseException (HTTP 400/409).
         */
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // Helper method to copy non-sensitive fields from DTO to Entity
    private void updateData(User entity, UserDTO dto) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
    }
}

/*
 ============================================================================
 DEPENDENCY INJECTION COMPARISON
 ============================================================================

 Field Injection:
 @Autowired
 private UserRepository repository;
 @Autowired
 private PasswordEncoder passwordEncoder;

 Is functionally equivalent to Constructor Injection:
 public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
     this.repository = repository;
     this.passwordEncoder = passwordEncoder;
 }

 In both cases, Spring locates the required beans in the ApplicationContext
 and injects them automatically. However, Constructor Injection provides:
 1. Immutability via 'final' keyword.
 2. Compile-time safety (cannot instantiate without dependencies).
 3. Better unit testability.
 ============================================================================
*/

/*
 Client (Postman / Frontend)
           │
           │ Sends HTTP POST /users
           │ Body JSON: { "name": "Domingos", "password": "123" }
           ▼
   UserController
           │
           │ Passes UserInsertDTO
           ▼
    UserService ──────► Calls passwordEncoder.encode("123")
           │                                 │
           │ Receives Hash                   ▼
           │ "$2a$10$f398Xz..." ◄────────────┘
           │
           │ Sets hashed password on Entity
           ▼
   UserRepository
           │
           │ Saves Entity into Database
           ▼
  Database (tb_user)
  Column 'password' stores: "$2a$10$f398Xz..." (TEXTO PURO NUNCA É SALVO!)
 */