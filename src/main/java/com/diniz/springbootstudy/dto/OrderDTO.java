package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Order;
import com.diniz.springbootstudy.entities.User;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - FIELD FILTER & CONTRACT DEFINITION
// ============================================================================
// Core Purpose:
// The DTO acts as an EXPLICIT FIELD FILTER over the JPA Entity (@Entity).
// You (the developer) decide exactly which fields are exposed and returned
// to the client in HTTP responses.
//
// Key Functions:
// - Custom Field Filtering: Selectively returns only client-facing attributes.
// - API Decoupling: The DTO separates the API contract from the JPA Entity.
// - Prevents Serialization Loops: Avoids infinite JSON recursion caused by
//   bidirectional JPA relationships (@OneToMany / @ManyToOne).
// - Controls Relationships: Instead of exposing the complete User entity,
//   the DTO can expose only the User information required by the API.
// ============================================================================

/**
 * Data Transfer Object representing the filtered Order data payload
 * for API requests and responses.
 *
 * Filters the {@link Order} entity to expose only the fields required
 * by the API: id, moment and client.
 */
@JsonRootName(value = "order")
public class OrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private java.time.Instant moment;
    private User client;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public OrderDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * Mainly useful for Unit Tests and when creating DTO objects directly.
     *
     * @param id Order ID
     * @param moment Order date and time
     * @param client User associated with the Order
     */
    public OrderDTO(Long id, java.time.Instant moment, User client) {
        this.id = id;
        this.moment = moment;
        this.client = client;
    }

    /**
     * Entity Conversion Constructor (PRODUCTION USE).
     *
     * Selectively maps the desired fields from the JPA {@link Order} entity
     * into {@link OrderDTO}.
     *
     * @param entity The source Order entity retrieved from the database.
     */
    public OrderDTO(Order entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.client = entity.getClient();
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public java.time.Instant getMoment() {
        return moment;
    }

    public void setMoment(java.time.Instant moment) {
        this.moment = moment;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }
}

/*
 ============================================================================
 DTO AS A FIELD FILTERING PIPELINE
 ============================================================================

 Database Table (tb_order)
       │
       ▼
 [ Order Entity ]  <-- Full Entity:
       │
       │              • id
       │              • moment
       │              • client
       │                    │
       │                    └──> User
       │
       │ (Conversion via new OrderDTO(entity))
       ▼
   [ OrderDTO ]    <-- Filtered API Payload:
       │
       │              • id
       │              • moment
       │              • client
       │
       ▼
 OrderResource    <-- Returns OrderDTO
       │
       ▼
 HTTP Client      <-- Receives Order JSON
 ============================================================================


 RELATIONSHIP BETWEEN ORDER AND USER
 ============================================================================

                  1                         N
        +----------------+          +----------------+
        |      User      |          |     Order      |
        +----------------+          +----------------+
        | id (PK)        | <--------| client_id (FK) |
        | name           |          | id (PK)        |
        | email          |          | moment         |
        +----------------+          +----------------+
                                          |
                                          |
                                   client -> User

 One User can have many Orders.
 Each Order belongs to one User.

 In the database:

     tb_order.client_id  ──────────>  tb_user.id
          (Foreign Key)                (Primary Key)
 ============================================================================
*/