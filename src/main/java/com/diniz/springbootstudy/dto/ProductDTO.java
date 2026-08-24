package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Product;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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

    @NotBlank(message = "Field 'name' is required")
    @Size(min = 3, max = 80, message = "Name must be between 3 and 80 characters")
    private String name;

    @NotBlank(message = "Field 'description' is required")
    @Size(min = 10, message = "Description must have at least 10 characters")
    private String description;

    @Positive(message = "Price must be a positive number")
    private Double price;

    private String imgUrl;

    @NotEmpty(message = "Product must have at least one category")
    private Set<CategoryDTO> categories = new HashSet<>();

    // ============================================================================
    // CONSTRUCTORS
    // ============================================================================

    /**
     * Default Constructor.
     * Required for JSON deserialization frameworks like Jackson.
     */
    public ProductDTO() {
    }

    /**
     * Partial Parameterized Constructor.
     * Useful when categories are not immediately required.
     *
     * @param id Product ID
     * @param name Product name
     * @param description Product description
     * @param price Product price
     * @param imgUrl Product image URL
     */
    public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
    }

    /**
     * Full Parameterized Constructor.
     * Useful for Unit Tests and direct DTO instantiation.
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

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * We removed this method because transformation logic is now fully delegated
     * to the ProductMapper component, keeping the DTO clean and decoupled from JPA entities.
     *
     * public ProductDTO(Product entity) {
     *     this.id = entity.getId();
     *     this.name = entity.getName();
     *     this.description = entity.getDescription();
     *     this.price = entity.getPrice();
     *     this.imgUrl = entity.getImgUrl();
     *
     *     if (entity.getCategories() != null) {
     *         this.categories = entity.getCategories().stream()
     *                 .map(CategoryDTO::new)
     *                 .collect(Collectors.toSet());
     *     }
     * }
     */

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