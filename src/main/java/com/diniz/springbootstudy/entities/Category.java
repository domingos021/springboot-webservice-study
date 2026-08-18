package com.diniz.springbootstudy.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// annotations to map jpa
@Entity
@Table(name = "tb_category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;


    // ============================================================================
    // JPA ASSOCIATIONS
    // ============================================================================

    /*
     * 1ª ->
     * A Category can be associated with many Products.
     *
     * We use a Set instead of a List because we want to guarantee
     * that there are no duplicate products associated with the same category.
     *
     * Set is an interface and cannot be instantiated directly.
     * To create an instance, we use its implementation class HashSet.
     *
     * The same concept applies to List.
     * List is also an interface and cannot be instantiated directly,
     * so we use its implementation class ArrayList.
     *
     * The collection should not be initialized as null.
     * Instead, we initialize it as an empty Set using new HashSet<>(),
     * allowing us to add elements without NullPointerException.
     *
     * mappedBy = "categories" indicates that Product is the owner
     * of this relationship because it contains the @JoinTable configuration.
     */

    /*
     * 2ª ->
     * We only use a getter when working with collections (List, Set),
     * because we do not replace the collection itself.
     *
     * We only add or remove elements from the existing collection
     * using methods like add() and remove().
     */
    //Relational mapping object of jpa
    @JsonIgnore // blocks the serialization of the products collection to avoid infinite recursion during JSON serialization
    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();


    public Category() {
    }


    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
        //we don´t put collection in the constructor
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

    /*
     * 3ª ->
     * We only use a getter when working with collections (List, Set),
     * because we do not replace the collection itself.
     *
     * We only add or remove elements from the existing collection
     * using methods like add() and remove().
     */

    // only getter and setter
    public Set<Product> getProducts() {
        return products;
    }




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}