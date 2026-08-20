package com.diniz.springbootstudy.dto.orderdto_exemplo_antigo;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.OrderItem;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

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
 * Data Transfer Object representing the filtered Order01 data payload
 * for API requests and responses.
 *
 * Filters the {@link Order01} entity to expose the required API fields:
 * id, moment, orderStatus, client, items, and total.
 */
@JsonRootName(value = "order01")
public class OrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT"
    )
    private Instant moment;

    private OrderStatus orderStatus;
    private User client;
    private Set<OrderItem> items = new HashSet<>();

    /**
     * Default constructor.
     *
     * Required by serialization/deserialization frameworks such as Jackson.
     */
    public OrderDTO() {
    }

    /**
     * Parameterized constructor.
     *
     * Mainly useful for unit tests and manual DTO creation.
     *
     * @param id Order identifier
     * @param moment Date and time when the order was placed
     * @param orderStatus Order status
     * @param client User associated with the order
     */
    public OrderDTO(Long id, Instant moment, OrderStatus orderStatus, User client) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
    }

    /**
     * Entity Conversion Constructor (Production Use).
     *
     * Converts an {@link Order01} entity into an OrderDTO01 by copying
     * the fields exposed by the API.
     *
     * @param entity Source entity retrieved from the database.
     */
    public OrderDTO(Order01 entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderStatus = entity.getOrderStatus();
        this.client = entity.getClient();
        this.items = entity.getItems();
    }

    // =========================================================================
    // GETTERS AND SETTERS
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    /**
     * Calculates the total value of the order.
     * Jackson automatically includes this in the JSON response as "total".
     */
    public Double getTotal() {
        double sum = 0.0;
        for (OrderItem item : items) {
            sum += item.getSubTotal();
        }
        return sum;
    }
}

/*
============================================================================
DTO AS A FIELD FILTERING PIPELINE
============================================================================

Database Table (tb_order_01)
       │
       ▼
[ Order01 Entity ]  <-- Full Entity
       │
       │              • id
       │              • moment
       │              • orderStatus
       │              • client ──────► User
       │              • items  ──────► OrderItem
       │
       │ (Conversion via new OrderDTO01(entity))
       ▼
[ OrderDTO01 ]      <-- Filtered API Payload
       │
       │              • id
       │              • moment
       │              • orderStatus
       │              • client
       │              • items
       │              • total (derived)
       │
       ▼
Order01Resource
       │
       ▼
HTTP Client
============================================================================


RELATIONSHIP BETWEEN ORDER01 AND USER
============================================================================

                 1                           N
       +----------------+           +------------------+
       |      User      |           |     Order01      |
       +----------------+           +------------------+
       | id (PK)        | <---------| client_id (FK)   |
       | name           |           | id (PK)          |
       | email          |           | moment           |
       +----------------+           | orderStatus      |
                                    +------------------+
                                              │
                                              │
                                       client -> User

One User can have many Order01 records.
Each Order01 belongs to exactly one User.

Database relationship:

    tb_order_01.client_id  ─────────►  tb_user.id
         (Foreign Key)                 (Primary Key)
============================================================================

 */