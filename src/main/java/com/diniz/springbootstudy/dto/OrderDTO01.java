package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - FIELD FILTER & CONTRACT DEFINITION
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

    @NotNull(message = "Field 'orderStatus' is required")
    private OrderStatus orderStatus;

    @Valid
    @NotNull(message = "Field 'client' is required")
    private UserDTO client;

    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private Set<OrderItemDTO> items = new HashSet<>();

    @Valid
    private PaymentDTO payment;

    // Default Constructor (required for Jackson)
    public OrderDTO01() {
    }

    /**
     * Parameterized Constructor (without items/payment).
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
     * Full Parameterized Constructor.
     *
     * @param id Order ID
     * @param moment Order date and time
     * @param orderStatus Order status Enum
     * @param client UserDTO associated with the Order
     * @param items Set of OrderItemDTOs
     * @param payment PaymentDTO associated with the Order
     */
    public OrderDTO01(Long id, Instant moment, OrderStatus orderStatus, UserDTO client, Set<OrderItemDTO> items, PaymentDTO payment) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
        this.items = items != null ? items : new HashSet<>();
        this.payment = payment;
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * We removed this method because transformation logic is now fully delegated
     * to the OrderMapper component, keeping the DTO clean and decoupled from JPA entities.
     *
     * public OrderDTO01(Order01 entity) { ... }
     */

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

    public void setItems(Set<OrderItemDTO> items) {
        this.items = items;
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
     */
    public Double getTotal() {
        double sum = 0.0;
        for (OrderItemDTO item : items) {
            if (item != null && item.getSubTotal() != null) {
                sum += item.getSubTotal();
            }
        }
        return sum;
    }
}