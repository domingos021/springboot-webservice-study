package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.OrderDTO;
import com.diniz.springbootstudy.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
 *
 * Responsible for exposing the HTTP endpoints related to Orders.
 *
 * Useful Commands:
 * - Run application via terminal: mvn spring-boot:run
 * - Test endpoint (Find All):
 *   GET http://localhost:8080/orders
 *
 * - Test endpoint (Find by ID):
 *   GET http://localhost:8080/orders/1
 */
@RestController // Handles HTTP requests and returns data as JSON.
@RequestMapping(value = "/orders")
public class OrderController {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     // The Spring Framework automatically injects the dependency
     // directly into the field using reflection.
     //
     // @Autowired
     // private OrderService service;
    */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // Spring automatically calls this constructor and injects
    // the OrderService bean.
    //
    // Advantages:
    // - Makes dependencies explicit and transparent.
    // - Allows the field to be marked as 'final'.
    // - Easier to instantiate in unit tests.
    // - Ensures the object is fully initialized.
    // =========================================================

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // ========================================================================
    // ENDPOINT: Find All Orders
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders
    // ========================================================================

    @GetMapping
    public ResponseEntity<List<OrderDTO>> findAll() {

        /*
         * Request flow:
         *
         * 1. Client sends GET /orders.
         * 2. OrderController receives the HTTP request.
         * 3. Controller delegates the operation to OrderService.
         * 4. OrderService requests the data from OrderRepository.
         * 5. Repository retrieves Order entities from the database.
         * 6. Service converts Order entities into OrderDTO objects.
         * 7. Controller returns the DTO list to the client.
         */

        List<OrderDTO> list = service.findAll();

        /*
         * ResponseEntity.ok() creates an HTTP 200 OK response.
         *
         * body(list) places the list of OrderDTO objects
         * inside the HTTP response body.
         */

        return ResponseEntity.ok().body(list);
    }

    // ========================================================================
    // ENDPOINT: Find Order by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders/1
    // ========================================================================

    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {

        /*
         * @PathVariable extracts the ID from the URL.
         *
         * Example:
         *
         * GET /orders/1
         *
         * id = 1
         *
         * The Controller delegates the search to OrderService.
         */

        OrderDTO dto = service.findById(id);

        return ResponseEntity.ok().body(dto);
    }
}

/*
 ============================================================================
 SPRING BOOT LAYERED ARCHITECTURE - ORDER
 ============================================================================

                 HTTP Request
                      │
                      ▼
         +--------------------------+
         |      OrderController     |
         |     @RestController      |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |       OrderService       |
         |         @Service         |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |     OrderRepository      |
         |       @Repository        |
         +--------------------------+
                      │
                      ▼
         +--------------------------+
         |       Order Entity       |
         |         @Entity          |
         +--------------------------+
                      │
                      ▼
                   Database


 ============================================================================
 ORDER RELATIONSHIP WITH USER
 ============================================================================

                 1                    N
         +---------------+      +---------------+
         |     User      |      |     Order     |
         +---------------+      +---------------+
         | id            |◄─────| client_id (FK)|
         | name          |      | id            |
         | email         |      | moment        |
         | phone         |      +---------------+
         +---------------+

         One User can have many Orders.
         Each Order belongs to one User.

         @ManyToOne
         @JoinColumn(name = "client_id")


 ============================================================================
 SPRING BEAN DEPENDENCY FLOW
 ============================================================================

 ApplicationContext
         │
         ├──► OrderRepository
         │
         ├──► OrderService
         │        │
         │        └── receives OrderRepository
         │
         └──► OrderController
                  │
                  └── receives OrderService


 ============================================================================
 REQUEST EXECUTION & RESPONSE FLOW
 ============================================================================

 HTTP Method   Endpoint          Controller Method    Service Method
 -----------   ---------------  -------------------  --------------
 GET           /orders          findAll()             findAll()
 GET           /orders/{id}     findById(id)          findById(id)


 ============================================================================
 DATABASE RELATIONSHIP CREATED BY HIBERNATE
 ============================================================================

 User table:

 CREATE TABLE tb_user (
     id BIGINT,
     email VARCHAR(255),
     name VARCHAR(255),
     password VARCHAR(255),
     phone VARCHAR(255),
     PRIMARY KEY (id)
 );


 Order table:

 CREATE TABLE tb_order (
     client_id BIGINT,
     id BIGINT,
     moment TIMESTAMP,
     PRIMARY KEY (id)
 );


 Foreign Key:

 ALTER TABLE tb_order
     ADD CONSTRAINT ...
     FOREIGN KEY (client_id)
     REFERENCES tb_user;


 Therefore:

 tb_order.client_id ─────────► tb_user.id

 ============================================================================
*/