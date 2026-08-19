package com.diniz.springbootstudy.entities;

import com.diniz.springbootstudy.entities.pk.OrderItemPk;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /*
     * The OrderItemPk object becomes the primary key of this entity.
     *
     * Instead of using a single ID value, OrderItem uses a composite key
     * composed of two references: Order and Product.
     *
     * The combination of order_id and product_id uniquely identifies
     * each OrderItem record.
     *
     * This ID contains two references, namely order and product, which are
     * foreign keys that link the OrderItem entity to the Order and Product entities.
     *
     * This composite key allows us to represent the many-to-many relationship
     * between orders and products, with additional attributes such as quantity
     * and price.
     */
    @EmbeddedId
    private OrderItemPk id = new OrderItemPk();
    /*
     * OrderItem
     *      |
     *      +-- id
     *           |
     *           +-- OrderItemPk (empty object)
     *                |
     *                +-- order = null
     *                +-- product = null
     *
     * When a new OrderItem object is created, the OrderItemPk object
     * is also created and assigned to the id attribute.
     *
     * At this moment, the composite key exists, but its references are empty.
     * The order and product values are null because the relationships have
     * not been assigned yet.
     *
     * Later, when the Order and Product entities are provided, the references
     * inside OrderItemPk are filled:
     *
     *      OrderItemPk
     *           |
     *           +-- order = Order(id = 1)
     *           +-- product = Product(id = 5)
     *
     * Together, these two references form the composite primary key
     * that uniquely identifies the OrderItem entity.
     */
    private Integer quantity;
    private Double price;

    public OrderItem() {
    }

    public OrderItem(Order01 order, Product product, Double price, Integer quantity) {
        id.setOrder(order);
        id.setProduct(product);
        this.price = price;
        this.quantity = quantity;

        /*
         * The composite key is built using the Order and Product references.
         *
         * These two associations together form the unique identifier
         * of the OrderItem entity.
         *
         * The key is created internally because it depends on the relationship
         * between Order and Product.
         */
    }


// Accessing the associated entities through the auxiliary class (OrderItemPk).
// The composite key contains the references to Order and Product.

    @JsonIgnore
    public Order01 getOrder() {
        return id.getOrder();
    }

    public void setOrder(Order01 order) {
        id.setOrder(order); // Sets the Order reference inside the composite key.
    }

    @JsonIgnore
    public Product getProduct() {
        return id.getProduct();
    }

    public void setProduct(Product product) {
        id.setProduct(product); // Sets the Product reference inside the composite key.
    }

    //-------------------------------------------------
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSubTotal() {
        return (price != null && quantity != null) ? price * quantity : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}


/*
OrderItem (Entity)
       |
       |
       v
+-------------------+
| @EmbeddedId       |
| OrderItemPk id    |
+-------------------+
       |
       |
       +------ order  -----> Order
       |
       +------ product ----> Product


Tabela:

tb_order_item

order_id | product_id | quantity | price
-----------------------------------------
    1    |      5      |    2     | 50.0
 */