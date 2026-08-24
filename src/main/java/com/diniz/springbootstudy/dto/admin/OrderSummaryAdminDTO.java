package com.diniz.springbootstudy.dto.admin;

import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

// ============================================================================
// ADMIN DTO - SUMMARIZED ORDER VIEW FOR PRODUCT MANAGEMENT
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

    // Default Constructor (required for Jackson)
    public OrderSummaryAdminDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param orderId Order ID
     * @param moment Order date and time
     * @param orderStatus Order status Enum
     * @param clientName Associated client's name
     * @param clientEmail Associated client's email
     */
    public OrderSummaryAdminDTO(Long orderId, Instant moment, OrderStatus orderStatus, String clientName, String clientEmail) {
        this.orderId = orderId;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * Transformation logic is now fully delegated to OrderSummaryAdminMapper.
     */

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