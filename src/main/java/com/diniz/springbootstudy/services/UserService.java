package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

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
// - Uses the Repository layer to communicate with the database.
// - Converts Entities to DTOs before returning data back to the Controller layer.
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 *
 * @Service indicates that this class holds business logic.
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
    // This service depends on UserRepository.
    // Spring automatically injects the UserRepository bean into this constructor.
    //
    // Advantages:
    // - Explicit dependency contract.
    // - Field can be marked as 'final' (immutability).
    // - Easy to mock in unit tests without Spring Context.
    // =========================================================
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    public List<UserDTO> findAll() {
        /*
         * Fetches User entities from the database and converts the list to UserDTO
         * using Java Streams and the UserDTO constructor reference.
         */
        List<User> list = repository.findAll();
        return list.stream().map(UserDTO::new).toList();
    }

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
}

/*
 ============================================================================
 DEPENDENCY INJECTION COMPARISON
 ============================================================================

 Field Injection:
 @Autowired
 private UserRepository repository;

 Is functionally equivalent to Constructor Injection:
 public UserService(UserRepository repository) {
     this.repository = repository;
 }

 In both cases, Spring locates the UserRepository bean in the ApplicationContext
 and injects it automatically. However, Constructor Injection provides:
 1. Immutability via 'final' keyword.
 2. Compile-time safety (cannot instantiate without dependencies).
 3. Better unit testability.
 ============================================================================
*/