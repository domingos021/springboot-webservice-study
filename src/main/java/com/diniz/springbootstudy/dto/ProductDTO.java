package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Product;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - FIELD FILTER & CONTRACT DEFINITION
// ============================================================================
// Core Purpose:
// The DTO acts as an EXPLICIT FIELD FILTER over the JPA Entity (@Entity).
// You (the developer) decide exactly which fields are exposed and returned
// to the client in HTTP responses.
//
// Key Functions:
// - Custom Field Filtering: Selectively returns only client-facing attributes.
// - API Decoupling: The DTO separates the API contract from the JPA Entity.
// - Prevents exposing unnecessary database fields.
// - Controls entity relationships exposed through the API.
// ============================================================================

/**
 * Data Transfer Object representing the filtered Product data payload
 * for API requests and responses.
 *
 * Filters the {@link Product} entity to expose only the fields required
 * by the API: id, name, description, price, imgUrl, and categories.
 */
@JsonRootName(value = "product")
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imgUrl;

    private Set<CategoryDTO> categories = new HashSet<>();

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public ProductDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * Mainly useful for Unit Tests and when creating DTO objects directly.
     *
     * @param id Product ID
     * @param name Product name
     * @param description Product description
     * @param price Product price
     * @param imgUrl Product image URL
     * @param categories Product categories
     */
    public ProductDTO(Long id,
                      String name,
                      String description,
                      Double price,
                      String imgUrl,
                      Set<CategoryDTO> categories
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.categories = categories;
    }

    /**
     * Entity Conversion Constructor (PRODUCTION USE).
     *
     * Selectively maps the desired fields from the JPA {@link Product} entity
     * into {@link ProductDTO}.
     *
     * @param entity The source Product entity retrieved from the database.
     */
    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.imgUrl = entity.getImgUrl();

        /*
         * Populates the categories set by converting each associated Category entity
         * into a CategoryDTO using Java Stream API.
         */
        if (entity.getCategories() != null) {
            this.categories = entity.getCategories().stream()
                    .map(CategoryDTO::new)
                    .collect(Collectors.toSet());
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

    public Set<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<CategoryDTO> categories) {
        this.categories = categories;
    }
}