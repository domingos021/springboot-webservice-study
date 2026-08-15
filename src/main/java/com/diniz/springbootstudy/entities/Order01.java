package com.diniz.springbootstudy.entities;

import com.diniz.springbootstudy.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Order01 Entity.
 *
 * =======================================================================================
 * EXECUTION FLOW AND ARCHITECTURE
 * =======================================================================================
 *
 *   JAVA / SPRING APPLICATION
 *
 *     Order01 (Entity)
 *     ┌──────────────────────────────────────────────┐
 *     │ private Long id;                             │
 *     │ private Instant moment;                      │
 *     │ private OrderStatus orderStatus; <─────────┐ │  (Works directly
 *     │ private User client;                       │ │   with the strongly-typed
 *     └────────────────────────────────────────────┼─┘   Enum!)
 *                                                  │
 *                                     @Convert (OrderStatusConverter)
 *                                     ┌─────────────┴────────────┐
 *                                     │ convertToDatabaseColumn  │ ──► Ex: OrderStatus.PAID -> 2
 *                                     │ convertToEntityAttribute │ ◄── Ex: 2 -> OrderStatus.PAID
 *                                     └─────────────┬────────────┘
 *                                                   │
 *   DATABASE                                        │
 *                                                   ▼
 *     Table: tb_order_01
 *     +----+-------------------------+--------------+-----------+
 *     | id | moment                  | order_status | client_id |
 *     +----+-------------------------+--------------+-----------+
 *     | 1  | 2026-08-14T16:30:00Z    |      2       |    100    | (Stores the
 *     +----+-------------------------+--------------+-----------+  Integer code)
 *
 * =======================================================================================
 * RELATIONSHIP DIAGRAM (UML)
 * =======================================================================================
 *
 *     +----------------+           +----------------+
 *     |     User       | 1       N |    Order01     |
 *     +----------------+-----------+----------------+
 *     | id (PK)        | <---------| client_id (FK) |
 *     | name           |           | id (PK)        |
 *     | email          |           | moment         |
 *     | phone          |           | orderStatus    |
 *     +----------------+           +----------------+
 *
 *   Many Orders (Order01) belong to the same Client (User).
 *   Each Order (Order01) belongs to only one Client (User).
 *
 * =======================================================================================
 */
@Entity
@Table(name = "tb_order_01") // Mapped to tb_order_01 to avoid conflicting with the original Order class
@JsonRootName(value = "order01")
public class Order01 implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key. Automatically generated identifier by the database (IDENTITY).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Instant when the order was placed.
     * Mapped and formatted in JSON following the ISO-8601 / UTC standard ("yyyy-MM-dd'T'HH:mm:ss'Z'").
     */
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timezone = "GMT"
    )
    private Instant moment;

    /**
     * Order status in Enum format.
     *
     * Conversion to Integer (database) and back to Enum (Java)
     * is performed seamlessly via 'OrderStatusConverter' (@Converter).
     */
    private OrderStatus orderStatus;

    /**
     * Mapping of the Many-to-One relationship (@ManyToOne) with the User entity.
     * The 'client_id' column stores the Foreign Key in the 'tb_order_01' table.
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    /**
     * Default no-args constructor required by JPA/Hibernate.
     */
    public Order01() {
    }

    /**
     * Full constructor with all entity fields.
     *
     * @param id Order identifier
     * @param moment Instant when the order was placed
     * @param orderStatus Order status Enum
     * @param client User object representing the client who placed the order
     */
    public Order01(Long id, Instant moment, OrderStatus orderStatus, User client) {
        this.id = id;
        this.moment = moment;
        this.orderStatus = orderStatus;
        this.client = client;
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

    // =========================================================================
    // EQUALS AND HASHCODE
    // =========================================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order01 order01 = (Order01) o;
        return id != null && Objects.equals(id, order01.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}