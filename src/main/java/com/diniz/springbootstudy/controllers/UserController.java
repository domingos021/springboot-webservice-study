package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// ============================================================================
// ARCHITECTURAL OVERVIEW OF LAYERS IN A SPRING BOOT PROJECT
// ============================================================================
// REST Controller
// Handles HTTP requests and returns responses.
//
// Service Layer
// Contains the business rules and application logic.
//
// Data Access Layer (Repository)
// Responsible for database communication and data persistence.
//
// Entity Layer
// Represents the database tables and domain objects.
//
// DTO Layer (Data Transfer Object)
// Used to transfer data between application layers without exposing Entities.
// ============================================================================

/**
 * REST Controller: Resource Layer
 * Responsible for exposing the HTTP endpoints related to Users.
 *
 * Useful Commands:
 * - Run application via terminal: mvn spring-boot:run
 * - Test endpoint (Find All): GET <a href="http://localhost:8080/users">all</a>
 * - Test endpoint (Find by ID): GET <a href="http://localhost:8080/users/1">searching by id</a>
 */
@RestController // Semantic annotation indicating this class handles HTTP requests and produces JSON/XML responses.
@RequestMapping(value = "/users")
public class UserController {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     // The Spring Framework automatically injects the dependency
     // directly into the field using reflection.
     //
     // Simple and concise, but not the recommended approach
     // for modern Spring Boot applications.
     //
     // @Autowired
     // private UserService service;
    */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // This is the current industry standard and the approach
    // recommended by the Spring Framework.
    //
    // Spring automatically calls this constructor and injects
    // the UserService bean (no explicit @Autowired needed on single constructors).
    //
    // Advantages:
    // - Makes dependencies explicit and transparent.
    // - Allows the field to be marked as 'final' (immutability).
    // - Easier to instantiate in unit tests (without reflection).
    // - Ensures the object is always fully initialized upon creation.
    // =========================================================
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // ========================================================================
    // ENDPOINT: Find All Users
    // HTTP Method: GET
    // URL Example: http://localhost:8080/users
    // ========================================================================
    @GetMapping // Specifically handles HTTP GET requests.
    public ResponseEntity<List<UserDTO>> findAll() {
        /*
         * The request flow:
         * 1. Controller delegates execution to the Service layer.
         * 2. Service layer applies business rules, queries Repository, and maps Entities to DTOs.
         * 3. Controller returns DTOs (Data Transfer Objects), protecting database entities from direct API exposure.
         */
        List<UserDTO> list = service.findAll();

        /*
         * ResponseEntity.ok() creates an HTTP 200 (OK) response builder.
         * body(list) sets the list of user DTOs as the HTTP response payload.
         */
        return ResponseEntity.ok().body(list);
    }

    // ========================================================================
    // ENDPOINT: Find User by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/users/1
    // ========================================================================
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable long id) {
        /*
         * Delegates lookup to Service layer, which converts the found User entity into a UserDTO.
         */
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }
}

/*
 ============================================================================
 SPRING BOOT LAYERED ARCHITECTURE (Dependency Order)
 ============================================================================

                 HTTP Request
                      │
                      ▼
         +--------------------------+
         |      UserController      |
         |     @RestController      |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |       UserService        |
         |         @Service         |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |     UserRepository       |
         |       @Repository        |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |       User Entity        |
         |         @Entity          |
         +--------------------------+
                      │
                      ▼
                   Database

 ============================================================================
 SPRING BEAN CREATION ORDER (Application Context Startup)
 ============================================================================

 ApplicationContext
         │
         ├──► UserRepository
         │
         ├──► UserService
         │        │
         │        └── receives UserRepository
         │
         └──► UserController
                  │
                  └── receives UserService

 ============================================================================
 REQUEST EXECUTION & RESPONSE FLOW
 ============================================================================

 HTTP Request
       │
       ▼
 UserController (HTTP handling & status codes)
       │
       ▼
 UserService    (Business rules, entity-to-DTO conversion)
       │
       ▼
 UserRepository (ORM / Database query execution)
       │
       ▼
 Database
       │
       ▲
 Response travels back up through the same layers as DTOs to the client.
 ============================================================================
*/