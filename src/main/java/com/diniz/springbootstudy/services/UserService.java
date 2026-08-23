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
//       ├──► UserMapper (@Component)  (Handles Entity <-> DTO conversions)
//       │
//       ▼
// UserRepository (@Repository)
//       │
//       ▼
// Database
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 * Coordinates business rules, entity mapping, and database access.
 */
@Service
public class UserService {

    private final UserRepository repository; //database connection
    private final UserMapper mapper; // security methods via dto

    public UserService(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    /*
     * Retrieves all users from the database and returns them as a List of UserDTO.
     * The UserDTO acts as an output filter, ensuring sensitive data (such as the password hash)
     * is completely omitted from the HTTP response payload.
     */
    public List<UserDTO> findAll() {
        List<User> list = repository.findAll(); // list to be transformed on the stream

        /*
         * STREAM PROCESSING PIPELINE:
         *
         * 1. list.stream():
         *    Converts the List<User> into a sequential Stream of User entities (on the conveyor belt).
         *
         * 2. .map(mapper::toDTO):
         *    Applies the UserMapper.toDTO() method to every User entity in the stream.
         *    Each User entity is mapped to a clean, filtered UserDTO.
         *
         *    SYNTAX EQUIVALENTS (How else this could be written):
         *
         *    a) Method Reference (USED HERE - Most concise & preferred):
         *       .map(mapper::toDTO)
         *
         *    b) Lambda Expression (Alternative functional style):
         *       .map(user -> mapper.toDTO(user))
         *
         *    c) Imperative Traditional For-Each Loop (Alternative manual assembly):
         *       List<UserDTO> dtos = new ArrayList<>();
         *       for (User user : list) {
         *           dtos.add(mapper.toDTO(user));
         *       }
         *       return dtos;
         *
         * 3. .toList():
         *    Collects the transformed UserDTO elements into an immutable List<UserDTO>.
         */
        return list.stream() // List<User> on the stream/conveyor belt ready to be processed
                .map(mapper::toDTO) // mapper::toDTO takes each User from the stream and transforms it into a UserDTO
                .toList(); // Collects the stream into a new, ready-to-use List<UserDTO>
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
         *    Returns an Optional<User> to handle the potential absence of data safely.
         *
         * 2. .orElseThrow(...):
         *    If the Optional contains a User, it unpacks and returns the User entity.
         *    If empty, it throws a ResourceNotFoundException using a Supplier Lambda (() -> ...).
         *
         * 3. mapper.toDTO(entity):
         *    Converts the retrieved managed User( this single id) entity into a UserDTO,
         *    filtering out the password hash before sending it to the Controller/Client.
         */
        User entity = repository.findById(id) // Searches for the user by ID (returns Optional<User>)
                .orElseThrow(() -> new ResourceNotFoundException(id)); // Unpacks entity OR throws 404 Exception

        return mapper.toDTO(entity); // Converts entity to UserDTO (hides sensitive fields)
    }


    /*
     * Ensures that every new user added to the database complies with the validation rules
     * established inside the UserInsertDTO (e.g., @NotBlank, @Email).
     *
     * Note: The password hashing is NOT handled by the UserInsertDTO itself, but rather
     * intercepted and encoded via BCrypt inside mapper.toEntity(dto).
     *
     * END-TO-END DATA FLOW:
     *
     * [ Client / Front-end ]
     *        │
     *        │ 1. Sends JSON with plain text password
     *        ▼
     *  UserInsertDTO (Input)
     *        │
     *        │ 2. Mapper converts to Entity + encodes via BCrypt
     *        ▼
     *   User Entity (Persisted to DB with generated ID)
     *        │
     *        │ 3. Mapper converts to UserDTO (Password filtered out)
     *        ▼
     *     UserDTO (Output / Return)
     *        │
     *        │ 4. Reaches Controller and becomes the JSON response body (HTTP 201 Created)
     *        ▼
     * [ Client / Front-end ]
     *
     *
     * INPUT / OUTPUT PAYLOAD STRUCTURE:
     *
     *       [ INPUT / REQUEST ]                          [ OUTPUT / RESPONSE ]
     *       UserInsertDTO (dto)                            UserDTO (Return)
     *   ┌─────────────────────────┐                  ┌───────────────────────────┐
     *   │ "name": "John",         │  ───► [Service]  │ "id": 15,                 │ ◄── Generated ID!
     *   │ "email": "john@a.com",  │       • BCrypt   │ "name": "John",           │
     *   │ "password": "123"       │  ───► • Persists │ "email": "john@a.com"     │
     *   └─────────────────────────┘                  └───────────────────────────┘
     *      (Carries plain password)                    (Password FILTERED OUT!)
     */
//        ▲                   ▲
//        │                   │
//     OUTPUT               INPUT
// (Response / HTTP 201)   (Request / HTTP Body)
    @Transactional
    public UserDTO insert(UserInsertDTO dto) {

        /*
         * CREATION & PERSISTENCE PIPELINE:
         *
         * 1. mapper.toEntity(dto):
         *    Converts the validated UserInsertDTO into a User entity and encodes the plain text password via BCrypt.
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
            // STEP 1: Map UserInsertDTO fields and encode the raw password into a new User entity
            User entity = mapper.toEntity(dto); // Populates validated fields into entity setters and encodes password

            // STEP 2: Persist validated entity to DB and receive generated ID
            entity = repository.save(entity); // Persists entity to DB and populates the auto-generated ID

            // STEP 3: Convert saved entity into a new UserDTO object (with generated ID) to return to client
            return mapper.toDTO(entity); // Returns safe UserDTO to the client

        } catch (DataIntegrityViolationException e) {
            // Protects against database-level unique constraint violations (e.g., duplicate email)
            throw new DatabaseException("Email already exists: " + dto.getEmail());
        }
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            mapper.updateEntityFromDTO(dto, entity);
            entity = repository.save(entity);
            return mapper.toDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
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
}