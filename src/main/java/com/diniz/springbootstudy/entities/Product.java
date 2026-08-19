package com.diniz.springbootstudy.entities;


import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;
    private String imgUrl;

// ============================================================================
// JPA ASSOCIATIONS / ENTITY RELATIONSHIPS
// ============================================================================

    /*
     * This collection represents a relationship between entities.
     *
     * The collection is not manually populated when the entity object is created.
     * It starts as an empty collection using new HashSet<>().
     *
     * The collection is managed by JPA/Hibernate.
     *
     * When this entity is loaded from the database, Hibernate automatically
     * populates this collection with the related entities according to the
     * mapping configuration (@OneToMany, @ManyToMany, etc.).
     *
     * Example:
     *
     * Entity
     *    |
     *    | collection
     *    |
     *    v
     * Related entities
     *
     * The relationship can be navigated through this collection, allowing access
     * to the associated objects.
     *
     * Note:
     * For new objects created only in memory, Hibernate has not loaded anything
     * yet. If necessary, the developer must maintain both sides of a bidirectional
     * relationship manually by adding or removing elements from the collections.
     */

    /*
     * A Product can have one or more Categories,
     * and a Category can be associated with one or more Products.
     *
     * We use a Set instead of a List because we want to guarantee
     * that the same product cannot have duplicate category associations.
     *
     * Set is an interface and cannot be instantiated directly.
     * To create an instance, we use its implementation class HashSet.
     *
     * The collection should not be initialized as null.
     * Instead, we initialize it as an empty Set using new HashSet<>(),
     * allowing us to add elements without NullPointerException.
     *
     * The same concept applies to List.
     * List is also an interface and cannot be instantiated directly,
     * so we use its implementation class ArrayList.
     *
     * Example:
     * List<Category> categories = new ArrayList<>();
     *
     * Both HashSet and ArrayList create empty collections ready
     * to store elements.
     *
     * Product is the owner side of this relationship because it defines
     * the @JoinTable configuration.
     */
    @ManyToMany
// Defines a many-to-many relationship between Product and Category.
// A Product can have many Categories, and a Category can have many Products.

    @JoinTable(
            name = "tb_product_category",
            // Defines the name of the intermediate join table created in the database.
            // This table stores the relationship between products and categories.


            joinColumns = @JoinColumn(name = "product_id"),
            // Defines the foreign key column that references the owner entity (Product).
            // The column product_id stores the Product primary key inside the join table.


            inverseJoinColumns = @JoinColumn(name = "category_id")
            // Defines the foreign key column that references the inverse entity (Category).
            // The column category_id stores the Category primary key inside the join table.
    )
    private Set<Category> categories = new HashSet<>();


    /*
     * OrderItem has an embedded id of type OrderItemPk.
     *
     * OrderItemPk is an auxiliary class that represents the composite key.
     * Inside this composite key we have two associations:
     *
     * id.order   -> gets the Order01 associated with this OrderItem
     * id.product -> gets the Product associated with this OrderItem
     *
     * Therefore, through the id attribute of OrderItem we can access both
     * sides of the composite key:
     *
     * orderItem.id.order
     * orderItem.id.product
     *
     * The mappedBy uses "id.product" because this collection is mapping
     * the relationship from Product to OrderItem through the product
     * association inside OrderItemPk.
     */

    /*
     * This collection represents the relationship between Product and OrderItem.
     *
     * Product does not manually populate this Set when the object is created.
     * The collection is managed by JPA/Hibernate.
     *
     * When a Product entity is loaded from the database, Hibernate automatically
     * fills this collection with all OrderItem records where:
     *
     * OrderItem.id.product == this Product
     *
     * The relationship is mapped through the composite key:
     *
     * Product
     *    |
     *    | orderItems
     *    |
     *    v
     * OrderItem
     *    |
     *    | id (OrderItemPk)
     *    |
     *    +-- product -> Product
     *    +-- order   -> Order01
     *
     * The OrderItemPk object contains the two associations that form the
     * composite key:
     *
     * id.product -> accesses the Product
     * id.order   -> accesses the Order01
     *
     * Therefore, this collection allows navigation from Product to OrderItem,
     * and through each OrderItem we can access the related Order01.
     *
     * Example:
     *
     * product.getOrders()
     *
     * Product -> OrderItem -> Order01
     *
     * Note:
     * The collection starts empty when a new Product object is created.
     * It is populated automatically by Hibernate when the entity is retrieved
     * from the database.
     */
    @OneToMany(mappedBy = "id.product")
    private Set<OrderItem> orderItems = new HashSet<>();



    public Product() {
    }

    public Product(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;

        /*
         * We do not include collections in the constructor because they are already
         * initialized when the object is created.
         *
         * The collection starts as an empty Set using new HashSet<>(),
         * and elements can be added later using add().
         */
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    /*
     * 3ª ->
     * We only use a getter when working with collections (List, Set),
     * because we do not replace the collection itself.
     *
     * We only add or remove elements from the existing collection
     * using methods like add() and remove().
     */
    public Set<Category> getCategories() {
        return categories;
    }


    // gets the Orders associated with this products
    public Set<Order01> getOrders() {
        Set<Order01> set = new HashSet<>();

        /*
         * For each OrderItem associated with this Product,
         * we get the Order01 object related to that item.
         *
         * The relationship is stored inside OrderItem through
         * its composite key (OrderItemPk).
         *
         * OrderItem
         *      |
         *      +-- id (OrderItemPk)
         *             |
         *             +-- order -> Order01
         *             +-- product -> Product
         *
         * The getOrder() method internally accesses:
         *
         * orderItem.id.order
         *
         * and returns the Order01 associated with this OrderItem.
         *
         * Then we add this Order01 object to the Set collection.
         *
         * Result:
         * Product -> OrderItem -> Order01
         */
        for (OrderItem orderItem : orderItems) {
            set.add(orderItem.getOrder());
        }

        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}