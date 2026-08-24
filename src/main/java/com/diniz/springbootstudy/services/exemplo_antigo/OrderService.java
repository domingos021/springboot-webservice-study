package com.diniz.springbootstudy.services.exemplo_antigo;

import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.mappers.OrderMapper;
import com.diniz.springbootstudy.repositories.OrderRepository;
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
// OrderResource (@RestController)
//       │
//       ▼
// OrderService (@Service)   ◄── Current class
//       │
//       ▼
// OrderRepository (@Repository)
//       │
//       ▼
// Database
//
// Responsibilities:
// - Contains business rules and application logic for Order.
// - Receives requests/data from the Controller layer.
// - Uses the Repository layer to communicate with the database.
// - Handles persistence exceptions and database integrity constraints.
// - Converts Order entities to OrderDTOs before returning data
//   back to the Controller layer.
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 *
 * Service indicates that this class holds business logic.
 * Spring automatically manages its lifecycle, allowing it to be injected
 * into controllers or other services.
 */
@Service
public class OrderService {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     // The Spring Framework automatically injects the dependency
     // directly into the field using reflection.
     //
     // @Autowired
     // private OrderRepository repository;
    */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // This service depends on OrderRepository and OrderMapper.
    // Spring automatically injects the beans into this constructor.
    //
    // Advantages:
    // - Explicit dependency contract.
    // - Fields can be marked as 'final' (immutability).
    // - Easy to mock in unit tests without Spring Context.
    // =========================================================
    private final OrderRepository repository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository repository, OrderMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {
        /*
         * Fetches Order entities from the database and converts the list
         * into a list of OrderDTOs using Java Streams and OrderMapper.
         */
        List<Order01> list = repository.findAll();

        /*
         * List<Order>
         *      ↓
         *   stream()
         *      ↓
         *    map()
         *      ↓
         * Order → OrderDTO
         *      ↓
         * List<OrderDTO>
         *
         * ----------------------------------------------------------
         * Equivalent lambda expression:
         *
         * return list.stream()
         *         .map(order -> mapper.toDTO(order))
         *         .toList();
         *
         * For each Order in the list, the lambda receives the 'order'
         * object and delegates transformation to the OrderMapper component.
         */

        // For each Order in the list, calls mapper::toDTO
        // and transforms the list of Orders into a list of OrderDTOs.
        return list.stream() // Starts a stream from the list of Orders
                .map(mapper::toDTO) // Converts each Order entity to OrderDTO01 via mapper
                .toList(); // Converts the Stream<OrderDTO01> into a List<OrderDTO01>
    }

    @Transactional(readOnly = true)
    public OrderDTO01 findById(Long id) {
        /*
         * Handling Optional<Order>:
         *
         * Instead of returning null (which yields an empty 200 OK), we throw
         * a custom ResourceNotFoundException when the ID is not present
         * in the database.
         *
         * The exception is caught globally by @ControllerAdvice
         * to return a proper 404 Not Found.
         */
        Order01 entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return mapper.toDTO(entity);
    }

    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {
        /*
         * Creates a new Order entity from the input OrderDTO via OrderMapper.
         *
         * The Order entity is then persisted through OrderRepository.
         * Finally, the persisted entity is converted back to OrderDTO
         * before being returned to the Controller layer.
         */
        Order01 entity = mapper.toEntity(dto);

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public OrderDTO01 update(Long id, OrderDTO01 orderdto) {
        /*
         * Uses getReferenceById(id) instead of findById(id) to avoid
         * an extra SELECT database query.
         *
         * JPA prepares a monitored entity proxy and only executes
         * the UPDATE query when the transaction commits.
         *
         * Catches EntityNotFoundException when the specified ID
         * does not exist in the database, rethrowing a custom
         * ResourceNotFoundException (HTTP 404).
         */
        try {
            Order01 entity = repository.getReferenceById(id);

            mapper.copyDtoToEntity(orderdto, entity);

            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    public void delete(Long id) {
        /*
         * Checks if the Order exists before trying to delete.
         *
         * Catches DataIntegrityViolationException if the entity
         * cannot be deleted because of database integrity constraints.
         *
         * For example, an Order may have relationships with other
         * entities that prevent its deletion.
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
}

/*
 ============================================================================
 DEPENDENCY INJECTION
 ============================================================================

 Constructor Injection:

 private final OrderRepository repository;
 private final OrderMapper mapper;

 public OrderService(OrderRepository repository, OrderMapper mapper) {
     this.repository = repository;
     this.mapper = mapper;
 }

 Spring locates the OrderRepository and OrderMapper beans in the ApplicationContext
 and injects them automatically into the OrderService constructor.

 Advantages:
 1. Immutability via the 'final' keyword.
 2. Compile-time safety.
 3. Better unit testability.
 4. Explicit dependency contract.
 ============================================================================
*/

/*
 Client (Postman / Frontend)
           │
           │ Sends HTTP POST /orders
           │ Body JSON: { "moment": "2026-08-12T16:00:00Z" }
           ▼
   OrderResource
           │
           │ Passes OrderDTO
           ▼
    OrderService
           │
           │ Converts DTO → Entity via OrderMapper
           ▼
   OrderRepository
           │
           │ Saves Entity
           ▼
  Database (tb_order)
           │
           ├── id
           ├── moment
           └── client_id (FK)
                    │
                    ▼
                 tb_user.id
*/

/*
 ============================================================================
 ORDER ↔ USER RELATIONSHIP
 ============================================================================

                         1
              +-------------------+
              |       User        |
              +-------------------+
              | id (PK)           |
              | name              |
              | email             |
              +-------------------+
                       │
                       │ orders
                       │
                       │ N
              +-------------------+
              |       Order       |
              +-------------------+
              | id (PK)           |
              | moment            |
              | client_id (FK)    |
              +-------------------+

 One User can have many Orders.
 Each Order belongs to one User.

 The Order table stores the foreign key:

     tb_order.client_id  ──────────>  tb_user.id

 ============================================================================
*/