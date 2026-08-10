package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.dto.UserInsertDTO;
import com.diniz.springbootstudy.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
 * - Test endpoint (Insert): POST <a href="http://localhost:8080/users">creating a new user</a>
 * - Test endpoint (Update): PUT <a href="http://localhost:8080/users/1">updating an existing user</a>
 * - Test endpoint (Delete): DELETE <a href="http://localhost:8080/users/1">deleting user by id</a>
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
    //mvn spring-boot:run
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
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        /*
         * Delegates lookup to Service layer, which converts the found User entity into a UserDTO.
         */
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // ========================================================================
    // ENDPOINT: Insert New User
    // HTTP Method: POST
    // URL Example: http://localhost:8080/users
    // Status Code: 201 Created (with 'Location' header pointing to the new resource)
    // ========================================================================
    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody UserInsertDTO dto) {
        /*
         * @RequestBody:
         * Deserializes the incoming JSON body payload directly into the UserInsertDTO object.
         *
         * ServletUriComponentsBuilder:
         * Standard Spring helper to generate the HTTP 'Location' header containing the URI
         * of the newly created resource (e.g., http://localhost:8080/users/3).
         */
        UserDTO newDto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newDto.getId())
                .toUri();

        /*
         * ResponseEntity.created(uri) generates HTTP Status 201 (Created)
         * with the Location header and returns the filtered UserDTO in the response body.
         */
        return ResponseEntity.created(uri).body(newDto);
    }

    // ========================================================================
    // ENDPOINT: Update Existing User
    // HTTP Method: PUT
    // URL Example: http://localhost:8080/users/1
    // Status Code: 200 OK
    // ========================================================================
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        /*
         * Replaces or updates fields of an existing user resource identified by 'id'.
         * Returns HTTP 200 (OK) with the updated UserDTO payload.
         */
        UserDTO updatedDto = service.update(id, dto);
        return ResponseEntity.ok().body(updatedDto);
    }

    // ========================================================================
    // ENDPOINT: Delete User by ID
    // HTTP Method: DELETE
    // URL Example: http://localhost:8080/users/1
    // Status Code: 24 No Content
    // ========================================================================
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        /*
         * Removes the user resource identified by 'id' from the database.
         * ResponseEntity.noContent() returns HTTP Status 204 (No Content)
         * which is the RESTful standard when a resource is successfully deleted
         * and no response body is returned.
         */
        service.delete(id);
        return ResponseEntity.noContent().build();
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
         │        └── receives UserRepository & PasswordEncoder
         │
         └──► UserController
                  │
                  └── receives UserService

 ============================================================================
 REQUEST EXECUTION & RESPONSE FLOW (CRUD VERBS)
 ============================================================================

 HTTP Method   Endpoint       Controller Method    Service Method    HTTP Status Code
 -----------   ------------   -----------------    --------------    ----------------
 GET           /users         findAll()            findAll()         200 OK
 GET           /users/{id}    findById(id)         findById(id)      200 OK (or 404)
 POST          /users         insert(dto)          insert(dto)       201 Created
 PUT           /users/{id}    update(id, dto)      update(id, dto)   200 OK (or 404)
 DELETE        /users/{id}    delete(id)           delete(id)        204 No Content
 ============================================================================
*/