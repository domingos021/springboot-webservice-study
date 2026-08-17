package com.diniz.springbootstudy.dto;

import com.diniz.springbootstudy.entities.Category;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serial;
import java.io.Serializable;

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
// - Defines the contract between the API and its consumers.
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
    private String name;

    // Default Constructor (required for JSON deserialization frameworks like Jackson)
    public CategoryDTO() {
    }

    /**
     * Parameterized Constructor.
     *
     * Mainly useful for Unit Tests and when creating DTO objects directly.
     *
     * @param id Category ID
     * @param name Category name
     */
    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Entity Conversion Constructor (PRODUCTION USE).
     *
     * Selectively maps the desired fields from the JPA {@link Category} entity
     * into {@link CategoryDTO}.
     *
     * @param entity The source Category entity retrieved from the database.
     */
    public CategoryDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

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

/*
 ============================================================================
 DTO AS A FIELD FILTERING PIPELINE
 ============================================================================

 Database Table (tb_category)
       │
       ▼
 [ Category Entity ]  <-- Full Entity:
       │
       │              • id
       │              • name
       │
       │ (Conversion via new CategoryDTO(entity))
       ▼
 [ CategoryDTO ]      <-- Filtered API Payload:
       │
       │              • id
       │              • name
       │
       ▼
 CategoryController   <-- Returns CategoryDTO
       │
       ▼
 HTTP Client          <-- Receives Category JSON
 ============================================================================
*/