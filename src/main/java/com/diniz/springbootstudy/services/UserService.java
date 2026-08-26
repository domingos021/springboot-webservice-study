package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.UserDTO;
import com.diniz.springbootstudy.dto.UserInsertDTO;
import com.diniz.springbootstudy.dto.UserUpdateDTO;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.mappers.UserMapper;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 ====================================================================================================
                  STUDY GUIDE: CREATED AND UPDATED LAYERS (FOCUSED)
 ====================================================================================================

 This guide is structured in chronological order of dependencies. Studying in this sequence allows
 you to understand how JWT-based security, password recovery, and self-profile management (/users/me)
 seamlessly connect from the database all the way to the HTTP routes.

 ====================================================================================================
 LOGICAL STUDY SEQUENCE (WHERE TO START AND HOW TO PROCEED)
 ====================================================================================================

 1. ENTITIES AND DTOs (The Database Foundation & Security Contracts) ◄── [ START HERE! ]
    -------------------------------------------------------------------------------------------------
    What to study first: The data structures required to support security features and resets.

    Reading Sequence:
      1.1. PasswordResetToken (Entity):
           - JPA mapping for the 'tb_password_reset_token' table.
           - Understand the @OneToOne relationship with the User entity.
           - Observe the isExpired() method comparing token expiration against Instant.now().
      1.2. ForgotPasswordDTO & ResetPasswordDTO (DTOs / Records):
           - Understand input validation annotations (@NotBlank, @Email, @Size).
           - ForgotPasswordDTO: Carries only the email provided in "Forgot Password".
           - ResetPasswordDTO: Carries the UUID token and the new plain-text password.

 2. DATA ACCESS LAYER (Token Repository)
    -------------------------------------------------------------------------------------------------
    What to study next: The bridge between the database and recovery operations.

    Reading Sequence:
      2.1. PasswordResetTokenRepository:
           - Derived Query findByToken(String token): Used to locate the received token.
           - Derived Query findByUser(User user): Used to purge obsolete user tokens.

 3. EMAIL SERVICE LAYER (Abstraction and Sending)
    -------------------------------------------------------------------------------------------------
    What to study next: How email communication is decoupled from business rules.

    Reading Sequence:
      3.1. EmailService (Interface): Simple contract specifying the sendPasswordResetEmail() method.
      3.2. MockEmailService (Implementation): How email dispatch is simulated by logging tokens to the console.

 4. JWT SECURITY LAYER (Token, Filter, and Context)
    -------------------------------------------------------------------------------------------------
    What to study next: The infrastructure that generates, validates, and injects user identity into the API.

    Reading Sequence:
      4.1. TokenService:
           - generateToken(User user): HMAC256 signing mechanism with a 2-hour expiration window.
           - validateToken(String token): Decoding and extracting user email (subject) from the JWT.
      4.2. JwtAuthenticationFilter:
           - How the filter reads the 'Authorization: Bearer <token>' header.
           - How it injects the authenticated user credentials into Spring's SecurityContextHolder.

 5. BUSINESS SERVICES (Password Reset & Profile Management Rules)
    -------------------------------------------------------------------------------------------------
    What to study next: The orchestration layer where security mechanisms merge with business logic.

    Reading Sequence:
      5.1. PasswordResetService:
           - createPasswordResetToken(): Purges old tokens, generates a 30-min UUID, and calls EmailService.
           - resetPassword(): Validates expiration, encrypts new password via BCrypt, and deletes consumed token.
      5.2. UserService (getMe() and updateMe() methods):
           - Usage of SecurityContextHolder.getContext().getAuthentication().getName().
           - How logged-in user profile details are fetched and updated without exposing IDs in URLs.

 6. REST CONTROLLERS (Exposing /auth and /users/me Routes)
    -------------------------------------------------------------------------------------------------
    What to study last: HTTP entry points receiving DTOs and delegating tasks to services.

    Reading Sequence:
      6.1. AuthenticationController (Updated):
           - Injection of PasswordResetService.
           - Public endpoints: POST /auth/login, POST /auth/forgot-password, and POST /auth/reset-password.
      6.2. UserController (Updated):
           - Private routes for logged-in user: GET /users/me and PUT /users/me.
           - Admin routes: GET /users, GET /users/{id}, PUT /users/{id}, and DELETE /users/{id}.

 ====================================================================================================
 END-TO-END LOGGED-IN USER EXECUTION FLOW (EXAMPLE: GET /users/me)
 ====================================================================================================

  1. Client authenticates via POST /auth/login ──► TokenService generates JWT.
  2. Client issues GET /users/me sending Header "Authorization: Bearer <token>".
  3. JwtAuthenticationFilter intercepts token, validates it, and sets SecurityContextHolder.
  4. UserController invokes service.getMe().
  5. UserService reads email from SecurityContextHolder, queries UserRepository, and returns UserDTO.
 ====================================================================================================
*/

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
//       ├──► UserMapper (@Component)  (Handles Entity <-> DTO conversions & BCrypt/Role logic)
//       ├──► SecurityContextHolder    (Extracts logged-in user credentials from JWT)
//       │
//       ▼
// UserRepository (@Repository)
//       │
//       ▼
// Database
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 * Coordinates business rules, entity mapping, security context inspection, and database access.
 */
@Service
public class UserService {

    private final UserRepository repository; // Database connection
    private final UserMapper mapper; // Security methods & mapping via DTO

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS (ADMIN / GENERAL MANAGEMENT)
    // ========================================================================

    @Transactional(readOnly = true)
    /*
     * Retrieves all users from the database and returns them as a List of UserDTO.
     * The UserDTO acts as an output filter, ensuring sensitive data (such as the password hash)
     * is completely omitted from the HTTP response payload.
     */
    public List<UserDTO> findAll() {
        List<User> list = repository.findAll(); // List to be transformed on the stream

        /*
         * STREAM PROCESSING PIPELINE:
         *
         * 1. list.stream():
         *    Converts the List<User> into a sequential Stream of User entities.
         *
         * 2. .map(mapper::toDTO):
         *    Applies the UserMapper.toDTO() method to every User entity in the stream.
         *    Each User entity is mapped to a clean, filtered UserDTO.
         *
         * 3. .toList():
         *    Collects the transformed UserDTO elements into an immutable List<UserDTO>.
         */
        return list.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    /*
     * Retrieves a single User by ID and returns it as a secure UserDTO.
     * If the user is not found, it throws a custom ResourceNotFoundException,
     * which is intercepted by the Global Exception Handler (@ControllerAdvice) to return an HTTP 404.
     */
    public UserDTO findById(Long id) {

        /*
         * READ & EXCEPTION PIPELINE:
         *
         * 1. repository.findById(id):
         *    Executes a SELECT query looking for the ID.
         *    Returns an Optional<User> to handle potential absence of data safely.
         *
         * 2. .orElseThrow(...):
         *    If the Optional contains a User, it unpacks and returns the User entity.
         *    If empty, it throws a ResourceNotFoundException using a Supplier Lambda (() -> ...).
         *
         * 3. mapper.toDTO(entity):
         *    Converts the retrieved managed User entity into a UserDTO,
         *    filtering out the password hash before sending it to the Controller/Client.
         */
        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return mapper.toDTO(entity);
    }

    /*
     * Ensures that every new user added to the database complies with the validation rules
     * established inside the UserInsertDTO (e.g., @NotBlank, @Email).
     *
     * Note: Password hashing via BCrypt and default UserRole.CLIENT assignment
     * are handled during mapper.toEntity(dto) execution.
     *
     * END-TO-END DATA FLOW:
     *
     * [ Client / Front-end ]
     *        │
     *        │ 1. Sends JSON with plain text password
     *        ▼
     *  UserInsertDTO (Input)
     *        │
     *        │ 2. Mapper converts to Entity + encodes via BCrypt + assigns ROLE_CLIENT
     *        ▼
     *   User Entity (Persisted to DB with generated ID and authority)
     *        │
     *        │ 3. Mapper converts to UserDTO (Password filtered out)
     *        ▼
     *     UserDTO (Output / Return)
     *        │
     *        │ 4. Reaches Controller and becomes the JSON response body (HTTP 201 Created)
     *        ▼
     * [ Client / Front-end ]
     */
    @Transactional
    public UserDTO insert(UserInsertDTO dto) {

        /*
         * CREATION & PERSISTENCE PIPELINE:
         *
         * 1. mapper.toEntity(dto):
         *    Converts the validated UserInsertDTO into a User entity, encodes the raw
         *    password via BCrypt, and sets default UserRole.CLIENT.
         *
         * 2. repository.save(entity):
         *    Persists the entity into the database (generating the primary key / ID).
         *
         * 3. mapper.toDTO(entity):
         *    Converts the persisted entity (now with ID) into a safe UserDTO response, omitting the password hash.
         *
         * 4. catch (DataIntegrityViolationException):
         *    Intercepts database constraint failures (such as UNIQUE constraints on the email column)
         *    and translates them into a domain-specific DatabaseException for the Global Exception Handler.
         */
        try {
            // STEP 1: Map UserInsertDTO fields, encode password, and set default UserRole into a new User entity
            User entity = mapper.toEntity(dto);

            // STEP 2: Persist validated entity to DB and receive generated ID
            entity = repository.save(entity);

            // STEP 3: Convert saved entity into a new UserDTO object (with generated ID) to return to client
            return mapper.toDTO(entity);

        } catch (DataIntegrityViolationException e) {
            // Protects against database-level unique constraint violations (e.g., duplicate email)
            throw new DatabaseException("Email already exists: " + dto.getEmail());
        }
    }

    /*
     * Updates an existing User's editable fields (Name, Email, Phone) based on the provided UserUpdateDTO.
     *
     * Password and UserRole updates are intentionally excluded from this operation.
     */
    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {

        /*
         * UPDATE PIPELINE:
         *
         * 1. repository.getReferenceById(id):
         *    Instantiates a JPA managed Proxy entity for the given ID without querying DB immediately.
         *
         * 2. mapper.updateEntityFromDTO(dto, entity):
         *    Copies pre-validated fields from the DTO directly into the managed entity in memory.
         *
         * 3. JPA Dirty Checking & repository.save(entity):
         *    Hibernate monitors the managed entity and automatically flushes changes to DB.
         *
         * 4. catch Blocks:
         *    - EntityNotFoundException: Triggers when the requested ID does not exist in DB (returns 404).
         *    - DataIntegrityViolationException: Prevents updating email to one that already belongs to another user.
         */
        try {
            // STEP 1: Get JPA managed reference (Proxy) for the target user entity
            User entity = repository.getReferenceById(id);

            // STEP 2: Copy updated fields (Name, Email, Phone) from DTO into managed entity in memory
            mapper.updateEntityFromDTO(dto, entity);

            // STEP 3: Persist changes (JPA Dirty Checking handles the UPDATE query automatically)
            entity = repository.save(entity);

            // STEP 4: Convert updated entity into a safe UserDTO response and return to client
            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            // Intercepts non-existing ID errors and translates to domain ResourceNotFoundException (HTTP 404)
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            // Protects against database-level unique constraint violations (e.g., updating email to an existing one)
            throw new DatabaseException("Email already exists: " + dto.getEmail());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // ========================================================================
    // LOGGED USER WORKFLOWS (/users/me)
    // ========================================================================

    /*
     * END-TO-END DATA FLOW (GET CURRENTLY AUTHENTICATED USER):
     *
     * [ Client / Front-end ]
     *        │
     *        │ 1. Sends HTTP GET /users/me with Bearer Token Header
     *        ▼
     *  SecurityContextHolder
     *        │
     *        │ 2. Extracts authenticated user's email (Principal Subject)
     *        ▼
     *  UserRepository.findByEmail(email)
     *        │
     *        │ 3. Fetches target User entity from database
     *        ▼
     *     UserDTO (Output / Return)
     *        │
     *        │ 4. Converted via mapper and returned as HTTP 200 OK
     *        ▼
     * [ Client / Front-end ]
     */
    @Transactional(readOnly = true)
    public UserDTO getMe() {
        /*
         * STEP 1: Inspect the SecurityContextHolder populated by JwtAuthenticationFilter
         * to extract the authenticated username/email safely.
         */
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        /*
         * STEP 2: Retrieve the corresponding User entity from DB or throw domain exception.
         * Uses Spring Data JPA standard method: findByEmail(email)
         */
        User entity = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        /*
         * STEP 3: Map entity to clean output UserDTO payload (omitting password hash).
         */
        return mapper.toDTO(entity);
    }

    /*
     * Updates profile details (Name, Email, Phone) of the currently logged-in user.
     * Prevents modifying other users' resources by binding identity directly to the JWT token context.
     */
    @Transactional
    public UserDTO updateMe(UserUpdateDTO dto) {
        try {
            /*
             * STEP 1: Extract authenticated user email from SecurityContextHolder.
             */
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            /*
             * STEP 2: Fetch target managed entity from DB using findByEmail(email).
             */
            User entity = repository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

            /*
             * STEP 3: Copy validated fields from DTO to managed entity.
             */
            mapper.updateEntityFromDTO(dto, entity);

            /*
             * STEP 4: Persist changes and flush updates to DB.
             */
            entity = repository.save(entity);

            /*
             * STEP 5: Convert and return updated UserDTO.
             */
            return mapper.toDTO(entity);

        } catch (DataIntegrityViolationException e) {
            // Intercepts email duplicate constraints if user changes email to an already registered one
            throw new DatabaseException("Email already exists: " + dto.getEmail());
        }
    }
}