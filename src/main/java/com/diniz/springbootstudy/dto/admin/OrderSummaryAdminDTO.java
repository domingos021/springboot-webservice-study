package com.diniz.springbootstudy.dto.admin;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

// ============================================================================
// ADMIN DTO - SUMMARIZED ORDER VIEW FOR PRODUCT MANAGEMENT
// ============================================================================
// Core Purpose:
// Provides a lightweight summary of an Order specifically tailored for administrative
// product sales reports.
//
// Key Benefits:
// - Eliminates recursion: Does NOT include the list of OrderItems.
// - High Performance: Serializes only essential management attributes.
// ============================================================================

/**
 * Summarized Order DTO for administrative reporting.
 */
public class OrderSummaryAdminDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long orderId;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT"
    )
    private Instant moment;

    private OrderStatus orderStatus;
    private String clientName;
    private String clientEmail;

    public OrderSummaryAdminDTO() {
    }

    /**
     * Entity Conversion Constructor.
     *
     * Maps essential fields from the Order01 entity and its associated User.
     *
     * @param entity Source Order01 entity.
     */
    public OrderSummaryAdminDTO(Order01 entity) {
        this.orderId = entity.getId();
        this.moment = entity.getMoment();
        this.orderStatus = entity.getOrderStatus();

        if (entity.getClient() != null) {
            this.clientName = entity.getClient().getName();
            this.clientEmail = entity.getClient().getEmail();
        }
    }

    // ============================================================================
    // GETTERS AND SETTERS
    // ============================================================================

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }
}
