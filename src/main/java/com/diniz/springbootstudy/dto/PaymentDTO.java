package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

// ============================================================================
// PAYMENT DTO - REST RESPONSE CONTRACT
// ============================================================================

/**
 * Data Transfer Object for Payment details.
 */
public class PaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "Field 'moment' is required")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT"
    )
    private Instant moment;

    @NotNull(message = "Field 'orderId' is required")
    private Long orderId;

    // Default Constructor (required for Jackson)
    public PaymentDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param id Payment ID
     * @param moment Payment timestamp
     * @param orderId Associated Order ID
     */
    public PaymentDTO(Long id, Instant moment, Long orderId) {
        this.id = id;
        this.moment = moment;
        this.orderId = orderId;
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * We removed this method because transformation logic is now fully delegated
     * to the PaymentMapper component.
     *
     * public PaymentDTO(Payment entity) {
     *     this.id = entity.getId();
     *     this.moment = entity.getMoment();
     *     if (entity.getOrder() != null) {
     *         this.orderId = entity.getOrder().getId();
     *     }
     * }
     */

    // ============================================================================
    // GETTERS AND SETTERS
    // ============================================================================

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}