package com.diniz.springbootstudy.services.exemplo_antigo;

import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
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
    // This service depends on OrderRepository.
    // Spring automatically injects the repository bean into this constructor.
    //
    // Advantages:
    // - Explicit dependency contract.
    // - Fields can be marked as 'final' (immutability).
    // - Easy to mock in unit tests without Spring Context.
    // =========================================================
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {
        /*
         * Fetches Order entities from the database and converts the list
         * into a list of OrderDTOs using Java Streams and a constructor reference.
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
         *         .map(order -> new OrderDTO(order))
         *         .toList();
         *
         * For each Order in the list, the lambda receives the 'order'
         * object and explicitly creates a new OrderDTO by passing
         * 'order' to the constructor.
         */

        // For each Order in the list, calls the OrderDTO(Order) constructor
        // and transforms the list of Orders into a list of OrderDTOs.
        return list.stream() // Starts a stream from the list of Orders
                .map(OrderDTO01::new) // Creates a new OrderDTO for each Order
                .toList(); // Converts the Stream<OrderDTO> into a List<OrderDTO>
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

        return new OrderDTO01(entity);
    }

    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {
        /*
         * Creates a new Order entity from the input OrderDTO.
         *
         * The Order entity is then persisted through OrderRepository.
         * Finally, the persisted entity is converted back to OrderDTO
         * before being returned to the Controller layer.
         */
        Order01 entity = new Order01();

        // Copies the data received from the DTO to the entity.
        entity.setMoment(dto.getMoment());

        entity = repository.save(entity);

        return new OrderDTO01(entity);
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

            updateData(entity, orderdto);

            entity = repository.save(entity);

            return new OrderDTO01(entity);

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

    /*
     * Helper method responsible for updating the entity fields using data
     * received from the DTO.
     *
     * This method copies only the fields that are allowed to be changed,
     * keeping control over which entity attributes can be modified.
     *
     * It prevents duplicated code by centralizing the update logic in one place.
     *
     * Example:
     *
     * Order01 entity  <-  OrderDTO dto
     *
     * Only the fields defined here will be updated.
     * Fields not included remain unchanged.
     *
     * This approach is commonly used in update operations (PUT),
     * where we receive a DTO and apply its values to an existing entity.
     */
    private void updateData(Order01 entity, OrderDTO01 orderdto) {
        entity.setMoment(orderdto.getMoment());
    }
}

/*
 ============================================================================
 DEPENDENCY INJECTION
 ============================================================================

 Constructor Injection:

 private final OrderRepository repository;

 public OrderService(OrderRepository repository) {
     this.repository = repository;
 }

 Spring locates the OrderRepository bean in the ApplicationContext
 and injects it automatically into the OrderService constructor.

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
           │ Converts DTO → Entity
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