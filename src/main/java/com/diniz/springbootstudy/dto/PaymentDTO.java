package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

// ============================================================================
// PAYMENT DTO - REST RESPONSE CONTRACT
// ============================================================================
// Isolates the payment data and the associated order ID
// to prevent circular reference serialization issues when nested in OrderDTO01.
// ============================================================================

/**
 * Data Transfer Object for Payment details.
 */
public class PaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT"
    )
    private Instant moment;

    private Long orderId;

    public PaymentDTO() {
    }

    public PaymentDTO(Long id, Instant moment, Long orderId) {
        this.id = id;
        this.moment = moment;
        this.orderId = orderId;
    }

    /**
     * Converts a Payment entity into a PaymentDTO.
     *
     * @param entity Source Payment entity.
     */
    public PaymentDTO(Payment entity) {
        this.id = entity.getId();
        this.moment = entity.getMoment();
        if (entity.getOrder() != null) {
            this.orderId = entity.getOrder().getId();
        }
    }

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