package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
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
 * Data Transfer Object representing the filtered Order data payload
 * for API requests and responses.
 *
 * Filters the {@link Order01} entity to expose only the fields required
 * by the API: id, moment, orderStatus, client, items and total.
 */
@JsonRootName(value = "order")
public class OrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private OrderStatus orderStatus;

    /*
     * We use UserDTO instead of the JPA Entity (User).
     * This guarantees that no bidirectional JPA attributes from User (like Set<Order01>)
     * cause infinite serialization loops during JSON conversion.
     */
    private UserDTO client;

    /*
     * Collection of OrderItemDTO representing the items purchased in this order.
     * Maps the relationship without exposing the underlying JPA OrderItem entity directly.
     */
    private Set<OrderItemDTO> items = new HashSet<>();

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
     * @param orderStatus Order status Enum
     * @param client UserDTO associated with the Order
     */
    public OrderDTO(Long id, Instant moment, OrderStatus orderStatus, UserDTO client) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
    }

    /**
     * Entity Conversion Constructor (PRODUCTION USE).
     *
     * Selectively maps the desired fields from the JPA {@link Order01} entity
     * into {@link OrderDTO}.
     *
     * @param entity The source Order01 entity retrieved from the database.
     */
    public OrderDTO(Order01 entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderStatus = entity.getOrderStatus();

        if (entity.getClient() != null) {
            this.client = new UserDTO(entity.getClient());
        }

        /*
         * Populates the items set by converting each OrderItem entity from Order01
         * into an OrderItemDTO.
         */
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> this.items.add(new OrderItemDTO(item)));
        }
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

    public UserDTO getClient() {
        return client;
    }

    public void setClient(UserDTO client) {
        this.client = client;
    }

    public Set<OrderItemDTO> getItems() {
        return items;
    }

    /**
     * Calculates the total price of the order dynamically by summing up
     * the subtotal of each OrderItemDTO in the items collection.
     *
     * Jackson automatically serializes this method call into a 'total' key in the JSON response.
     */
    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : items) {
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
 [ Order01 Entity ] <-- Full Entity:
       │
       │              • id
       │              • moment
       │              • orderStatus
       │              • client  ──> User
       │              • items   ──> Set<OrderItem>
       │
       │ (Conversion via new OrderDTO(entity))
       ▼
   [ OrderDTO ]    <-- Filtered API Payload:
       │
       │              • id
       │              • moment
       │              • orderStatus
       │              • client (UserDTO)
       │              • items (Set<OrderItemDTO>)
       │              • total (Calculated dynamically)
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
        |      User      |          |    Order01     |
        +----------------+          +----------------+
        | id (PK)        | <--------| client_id (FK) |
        | name           |          | id (PK)        |
        | email          |          | moment         |
        +----------------+          | order_status   |
                                    +----------------+
                                          |
                                          |
                                   client -> User

 One User can have many Orders.
 Each Order belongs to one User.

 In the database:

     tb_order_01.client_id ──────────> tb_user.id
          (Foreign Key)                 (Primary Key)
 ============================================================================
*/