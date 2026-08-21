package com.diniz.springbootstudy.entities;

import com.diniz.springbootstudy.entities.converters.OrderStatusConverter;
import com.diniz.springbootstudy.entities.enums.OrderStatus;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
 *     +----------------+           +----------------+           +----------------+
 *     |     User       | 1       N |    Order01     | 1       N |   OrderItem    |
 *     +----------------+-----------+----------------+-----------+----------------+
 *     | id (PK)        | <---------| client_id (FK) | <---------| order_id (PK)  |
 *     | name           |           | id (PK)        |           | product_id(PK) |
 *     | email          |           | moment         |           | quantity       |
 *     | phone          |           | orderStatus    |           | price          |
 *     +----------------+-----------+----------------+-----------+----------------+
 *
 *   Many Orders (Order01) belong to the same Client (User).
 *   Each Order (Order01) belongs to only one Client (User).
 *   Each Order (Order01) can have many OrderItems.
 *
 * =======================================================================================
 */
@Entity
@Table(name = "tb_order_01")
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
     */
    private Instant moment;

    /**
     * Order status in Enum format.
     *
     * Conversion to Integer (database) and back to Enum (Java)
     * is performed seamlessly via 'OrderStatusConverter' (@Converter).
     */
    @Column(name = "order_status")
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus orderStatus;

    /**
     * Mapping of the Many-to-One relationship (@ManyToOne) with the User entity.
     * The 'client_id' column stores the Foreign Key in the 'tb_order_01' table.
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    /**
     * Set of OrderItem entities associated with this Order01.
     *
     * OrderItem has an embedded id of type OrderItemPk.
     *
     * OrderItemPk is an auxiliary class that represents the composite key
     * and contains two associations:
     *
     * id.order   -> references the Order01 associated with this OrderItem
     * id.product -> references the Product associated with this OrderItem
     *
     * Through the id attribute of OrderItem, we can access both associations:
     *
     * orderItem.id.order
     * orderItem.id.product
     *
     * The mappedBy = "id.order" means that this collection is mapped through
     * the 'order' attribute inside the OrderItemPk composite key.
     *
     * In other words, this collection returns all OrderItem records where:
     *
     * orderItem.id.order == this Order01
     */
    @OneToMany(mappedBy = "id.order")
    private Set<OrderItem> items = new HashSet<>();

    /*
     * One-to-One relationship mapping with the Payment entity.
     *
     * The 'payment' field is the inverse (non-owning) side of the relationship.
     * The 'mappedBy = "order"' attribute indicates that the 'order' field
     * in the Payment entity is the owning side and is responsible for
     * managing the relationship.
     *
     * The Payment entity uses @MapsId, meaning its primary key is shared
     * with the Order01 entity. As a result, each Order01 can have at most
     * one Payment, and both entities share the same identifier.
     *
     * CascadeType.ALL propagates all persistence operations (persist, merge,
     * remove, refresh, and detach) from Order01 to its associated Payment.
     */

    /*
     * In this case, Payment and Order share the same primary key value.
     *
     * This is not caused by CascadeType.ALL, but by the @MapsId annotation.
     * The @MapsId tells JPA that the Payment entity uses the Order entity's
     * primary key as its own primary key.
     *
     * CascadeType.ALL only propagates persistence operations from Order to Payment,
     * such as saving, updating, and removing the associated Payment.
     */
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Set<OrderItem> getItems() {
        return items;
    }

    /**
     * Calculates the total value of the order by summing the subtotal of each item.
     * The subtotal of each OrderItem is calculated using price × quantity.
     *
     * Order01
     *    |
     *    | contains many
     *    ↓
     * OrderItem
     *    |
     *    | calculates
     *    ↓
     * getSubTotal()
     *    |
     *    | price * quantity
     *    ↓
     * subtotal
     *    |
     *    | sum all subtotals
     *    ↓
     * getTotal()
     */

/*
 * Alternative implementation using a traditional FOR-EACH loop.
 *
 * Iterates through each OrderItem in the 'items' collection,
 * calls getSubTotal() to calculate the item's subtotal,
 * and accumulates the result into the total sum.
 *
public Double getTotal() {
    double sum = 0.0;

    for (OrderItem item : items) {
        sum += item.getSubTotal();
    }

    return sum;
}
*/


    /*
     * Stream processing flow:
     *
     * items.stream()
     *        |
     *        ↓
     * Places all OrderItems into the processing pipeline (stream).
     *        |
     *        ↓
     * .mapToDouble(OrderItem::getSubTotal)
     *        |
     *        ↓
     * For each OrderItem, executes getSubTotal().
     *        |
     *        ↓
     * Converts each OrderItem into a double value representing its subtotal.
     *        |
     *        ↓
     * .sum()
     *        |
     *        ↓
     * Adds all subtotal values together and returns the final order total.
     *
     *
     * Example:
     *
     * items:
     *
     * [OrderItem 1] → getSubTotal() → 181.00
     * [OrderItem 2] → getSubTotal() → 1250.00
     * [OrderItem 3] → getSubTotal() → 50.00
     *
     *
     * After mapToDouble():
     *
     * [181.00, 1250.00, 50.00]
     *
     *
     * After sum():
     *
     * 181.00 + 1250.00 + 50.00
     *
     * = 1481.00 (Final order total)
     */
    public Double getTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
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