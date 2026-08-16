package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.order01.OrderDTO01;
import com.diniz.springbootstudy.services.OrderService01;
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
 * Responsible for exposing the HTTP endpoints related to Order01.
 *
 * Useful Commands:
 * - Run application via terminal: mvn spring-boot:run
 *
 * - Test endpoint (Find All):
 *   GET http://localhost:8080/orders01
 *
 * - Test endpoint (Find by ID):
 *   GET http://localhost:8080/orders01/1
 */
@RestController
@RequestMapping(value = "/orders01")
public class OrderController01 {

    /*
     // =========================================================
     // FIELD INJECTION (Attribute Injection) - NOT RECOMMENDED
     // =========================================================
     //
     // @Autowired
     // private OrderService01 service;
     */

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================
    // Spring automatically calls this constructor and injects
    // the OrderService01 bean.
    //
    // Advantages:
    // - Makes dependencies explicit and transparent.
    // - Allows the field to be marked as 'final'.
    // - Easier to instantiate in unit tests.
    // - Ensures the object is fully initialized.
    // =========================================================

    private final OrderService01 service;

    public OrderController01(OrderService01 service) {
        this.service = service;
    }

    // =========================================================================
    // ENDPOINT: Find All Order01 Records
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders01
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<OrderDTO01>> findAll() {

        /*
         * Request flow:
         *
         * 1. Client sends GET /orders01.
         * 2. OrderController01 receives the HTTP request.
         * 3. Controller delegates the operation to OrderService01.
         * 4. OrderService01 requests the data from OrderRepository01.
         * 5. Repository retrieves Order01 entities from the database.
         * 6. Service converts Order01 entities into OrderDTO01 objects.
         * 7. Controller returns the DTO list to the client.
         */

        List<OrderDTO01> list = service.findAll();

        /*
         * ResponseEntity.ok() creates an HTTP 200 OK response.
         *
         * body(list) places the list of OrderDTO01 objects
         * inside the HTTP response body.
         */

        return ResponseEntity.ok().body(list);
    }

    // =========================================================================
    // ENDPOINT: Find Order01 by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders01/1
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO01> findById(@PathVariable Long id) {

        /*
         * @PathVariable extracts the ID from the URL.
         *
         * Example:
         *
         * GET /orders01/1
         *
         * id = 1
         *
         * The Controller delegates the search to OrderService01.
         */

        OrderDTO01 dto = service.findById(id);

        return ResponseEntity.ok().body(dto);
    }
}

/*
============================================================================
SPRING BOOT LAYERED ARCHITECTURE - ORDER01
============================================================================

                HTTP Request
                     │
                     ▼
        +----------------------------+
        |     OrderController01      |
        |      @RestController       |
        +----------------------------+
                     │
                     ▼
        +----------------------------+
        |       OrderService01       |
        |         @Service           |
        +----------------------------+
                     │
                     ▼
        +----------------------------+
        |     OrderRepository01      |
        |       @Repository          |
        +----------------------------+
                     │
                     ▼
        +----------------------------+
        |       Order01 Entity       |
        |         @Entity            |
        +----------------------------+
                     │
                     ▼
                  Database


============================================================================
ORDER01 RELATIONSHIP WITH USER
============================================================================

                1                         N
        +---------------+         +------------------+
        |     User      |         |     Order01      |
        +---------------+         +------------------+
        | id            |◄────────| client_id (FK)   |
        | name          |         | id               |
        | email         |         | moment           |
        | phone         |         | order_status     |
        +---------------+         +------------------+

One User can have many Order01 records.
Each Order01 belongs to one User.

@ManyToOne
@JoinColumn(name = "client_id")


============================================================================
SPRING BEAN DEPENDENCY FLOW
============================================================================

ApplicationContext
        │
        ├──► OrderRepository01
        │
        ├──► OrderService01
        │        │
        │        └── receives OrderRepository01
        │
        └──► OrderController01
                 │
                 └── receives OrderService01


============================================================================
REQUEST EXECUTION & RESPONSE FLOW
============================================================================

HTTP Method      Endpoint              Controller Method      Service Method
-----------      ------------------    ------------------     ----------------
GET              /orders01             findAll()              findAll()
GET              /orders01/{id}        findById(id)           findById(id)


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


Order01 table:

CREATE TABLE tb_order_01 (
    client_id BIGINT,
    id BIGINT,
    moment TIMESTAMP,
    order_status INTEGER,
    PRIMARY KEY (id)
);


Foreign Key:

ALTER TABLE tb_order_01
    ADD CONSTRAINT ...
    FOREIGN KEY (client_id)
    REFERENCES tb_user;


Therefore:

tb_order_01.client_id ─────────► tb_user.id

============================================================================

 */