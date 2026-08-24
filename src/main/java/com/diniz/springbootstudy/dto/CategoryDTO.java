package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Category;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// DATA TRANSFER OBJECT (DTO) LAYER - FIELD FILTER & CONTRACT DEFINITION
// ============================================================================

/**
 * Data Transfer Object representing the filtered Category data payload
 * for API requests and responses.
 *
 * Filters the {@link Category} entity to expose only the fields required
 * by the API: id and name.
 */
@JsonRootName(value = "category")
public class CategoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Field 'name' is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    // Default Constructor (required for Jackson)
    public CategoryDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * @param id Category ID
     * @param name Category name
     */
    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /*
     * Entity Conversion Constructor (REMOVED / DEPRECATED)
     *
     * We removed this method because transformation logic is now fully delegated
     * to the CategoryMapper component.
     *
     * public CategoryDTO(Category entity) {
     *     this.id = entity.getId();
     *     this.name = entity.getName();
     * }
     */

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

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
}