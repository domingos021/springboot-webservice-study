package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

    /*
    OrderDTO01  (Main DTO)
      │
      ├── contains ──>  Set<OrderItemDTO>  (Child DTO)
      │                   │
      │                   └── extracts data from ──> Product / OrderItem
      │
      └── contains ──>  PaymentDTO          (Dependent 1:1 DTO)
    */

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
//   bidirectional JPA relationships (@OneToMany / @ManyToOne / @OneToOne).
// - Controls Relationships: Exposes nested DTOs (UserDTO, PaymentDTO, OrderItemDTO)
//   instead of exposing raw domain entities.
// ============================================================================

/**
 * Data Transfer Object representing the filtered Order data payload
 * for API requests and responses.
 *
 * Filters the {@link Order01} entity to expose only the fields required
 * by the API: id, moment, orderStatus, client, items, payment and total.
 */
@JsonRootName(value = "order")
public class OrderDTO01 implements Serializable {

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

    /*
     * PaymentDTO representing the associated 1:1 payment details.
     * Isolates the Payment entity and avoids circular serialization loop.
     */
    private PaymentDTO payment;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public OrderDTO01() {
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
     * @param payment PaymentDTO associated with the Order
     */
    public OrderDTO01(Long id, Instant moment, OrderStatus orderStatus, UserDTO client, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
        this.payment = payment;
    }

    /**
     * Entity Conversion Constructor (PRODUCTION USE).
     *
     * Selectively maps the desired fields from the JPA {@link Order01} entity
     * into {@link OrderDTO01}.
     *
     * @param entity The source Order01 entity retrieved from the database.
     */
    public OrderDTO01(Order01 entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        this.orderStatus = entity.getOrderStatus();

        if (entity.getClient() != null) {
            this.client = new UserDTO(entity.getClient());
        }

        /*
         * Populates the items set by converting each OrderItem entity from Order01
         * into an OrderItemDTO using Java Stream API.
         */
        if (entity.getItems() != null) {
            this.items = entity.getItems().stream()
                    .map(OrderItemDTO::new)
                    .collect(Collectors.toSet());
        }

        /*
         * Maps the 1:1 Payment association into PaymentDTO if present.
         */
        if (entity.getPayment() != null) {
            this.payment = new PaymentDTO(entity.getPayment());
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

    public PaymentDTO getPayment() {
        return payment;
    }

    public void setPayment(PaymentDTO payment) {
        this.payment = payment;
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