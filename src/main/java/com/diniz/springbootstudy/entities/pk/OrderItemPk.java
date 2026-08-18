package com.diniz.springbootstudy.entities.pk;

import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.Product;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderItemPk implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /*
     * In this case, the primary key is composite.
     *
     * A helper class used to define a composite primary key.
     * It combines two associations that together uniquely identify
     * an OrderItem entity.
     *
     * This class will have two references:
     * one reference to Product and one reference to Order.
     *
     * The primary key is composed of two foreign keys:
     * order_id and product_id.
     *
     * Neither order_id nor product_id can uniquely identify an OrderItem
     * by itself. Together, they create a unique combination that identifies
     * a specific item inside an order.
     *
     * This class will not have constructors, only getters and setters
     * for the two references.
     */
    //column of order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order01 order;

    /*
     * Defines a many-to-one relationship with Order.
     *
     * Many OrderItems can belong to one Order.
     * The order_id column stores the foreign key reference to the Order entity.
     *
     * This reference is part of the composite primary key.
     */

    //column of product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /*
     * Defines a many-to-one relationship with Product.
     *
     * Many OrderItems can reference the same Product.
     * The product_id column stores the foreign key reference to the Product entity.
     *
     * This reference is part of the composite primary key.
     */


    public Order01 getOrder() {
        return order;
    }

    public void setOrder(Order01 order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemPk that = (OrderItemPk) o;
        return Objects.equals(order, that.order)
                && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product);
    }
}


/*
 * A database table represents an entire entity.
 * Each column represents an attribute or a reference to another entity.
 *
 * Simple attributes become regular columns, while relationships with
 * other entities are represented by foreign key columns.
 *
 *
 * Example:
 *
 *        Product Entity
 *              |
 *              |
 *              v
 *      +----------------+
 *      |  tb_product    |
 *      +----------------+
 *      | id             |
 *      | name           |
 *      | price          |
 *      | category_id    | -----> tb_category
 *      +----------------+
 *
 *
 * In this case:
 * - The table represents the Product entity.
 * - Columns represent its attributes.
 * - category_id is a foreign key that references another entity.
 *
 *
 * OrderItem example:
 *
 *        Order              Product
 *          |                   |
 *          |                   |
 *          v                   v
 *      +---------------------------+
 *      |       tb_order_item        |
 *      +---------------------------+
 *      | order_id                  |
 *      | product_id                |
 *      | quantity                  |
 *      | price                     |
 *      +---------------------------+
 *
 * order_id and product_id together form a composite key,
 * because both references are needed to uniquely identify
 * an OrderItem.
 */

/*
 * OrderItemPk is not an entity and does not represent a database table.
 *
 * It is an auxiliary class used by JPA to define a composite primary key
 * for the OrderItem entity.
 *
 * It combines two references:
 * one to Order and one to Product.
 *
 *
 *             OrderItem Entity
 *                    |
 *                    |
 *                    v
 *
 *          +----------------------+
 *          |    tb_order_item     |
 *          +----------------------+
 *          | order_id             |
 *          | product_id           |
 *          | quantity             |
 *          | price                |
 *          +----------------------+
 *
 *
 * OrderItemPk:
 *
 *          +----------------------+
 *          |    OrderItemPk       |
 *          +----------------------+
 *          | order                |
 *          | product              |
 *          +----------------------+
 *
 *
 * The combination of order_id and product_id forms
 * the composite primary key of OrderItem.
 *
 * Neither column can uniquely identify an OrderItem alone.
 * Together, they create a unique identifier.
 */