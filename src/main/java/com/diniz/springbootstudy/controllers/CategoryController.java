package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// ============================================================================
// ARCHITECTURAL OVERVIEW OF LAYERS IN A SPRING BOOT PROJECT
// ============================================================================
// REST Controller
// Handles HTTP requests and returns responses.
//
// Service Layer
// Contains the business rules and application logic.
//
// Data Access Layer (Repository)
// Responsible for database communication and data persistence.
//
// Entity Layer
// Represents the database tables and domain objects.
//
// DTO Layer (Data Transfer Object)
// Used to transfer data between application layers without exposing Entities.
// ============================================================================

/**
 * REST Controller: Resource Layer
 *
 * Responsible for exposing the HTTP endpoints related to Categories.
 *
 * Useful Commands:
 * - Run application via terminal: mvn spring-boot:run
 *
 * Test endpoints:
 * GET    http://localhost:8080/categories
 * GET    http://localhost:8080/categories/1
 * POST   http://localhost:8080/categories
 * PUT    http://localhost:8080/categories/1
 * DELETE http://localhost:8080/categories/1
 */
@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // ========================================================================
    // ENDPOINT: Find All Categories
    // HTTP Method: GET
    // URL Example: http://localhost:8080/categories
    // ========================================================================

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll() {

        List<CategoryDTO> list = service.findAll();

        return ResponseEntity.ok().body(list);
    }

    // ========================================================================
    // ENDPOINT: Find Category by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/categories/1
    // ========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {

        CategoryDTO dto = service.findById(id);

        return ResponseEntity.ok().body(dto);
    }

    // ========================================================================
    // ENDPOINT: Insert Category
    // HTTP Method: POST
    // URL Example: http://localhost:8080/categories
    // ========================================================================

    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto) {

        dto = service.insert(dto);

        URI uri = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    // ========================================================================
    // ENDPOINT: Update Category
    // HTTP Method: PUT
    // URL Example: http://localhost:8080/categories/1
    // ========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> update(
            @PathVariable Long id,
            @RequestBody CategoryDTO dto) {

        dto = service.update(id, dto);

        return ResponseEntity.ok().body(dto);
    }

    // ========================================================================
    // ENDPOINT: Delete Category
    // HTTP Method: DELETE
    // URL Example: http://localhost:8080/categories/1
    // ========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}

/*
 ============================================================================
 SPRING BOOT LAYERED ARCHITECTURE - CATEGORY
 ============================================================================

                 HTTP Request
                      │
                      ▼
         +---------------------------+
         |    CategoryController     |
         |      @RestController      |
         +---------------------------+
                      │
                      ▼
         +---------------------------+
         |      CategoryService      |
         |         @Service          |
         +---------------------------+
                      │
                      ▼
         +---------------------------+
         |    CategoryRepository     |
         |       @Repository         |
         +---------------------------+
                      │
                      ▼
         +---------------------------+
         |      Category Entity      |
         |         @Entity           |
         +---------------------------+
                      │
                      ▼
                   Database


 ============================================================================
 REQUEST EXECUTION & RESPONSE FLOW
 ============================================================================

 HTTP Method   Endpoint               Controller Method   Service Method
 -----------   --------------------   -----------------   --------------
 GET           /categories            findAll()           findAll()
 GET           /categories/{id}       findById(id)        findById(id)
 POST          /categories            insert(dto)         insert(dto)
 PUT           /categories/{id}       update(id,dto)      update(id,dto)
 DELETE        /categories/{id}       delete(id)          delete(id)


 ============================================================================
 DATABASE TABLE
 ============================================================================

 CREATE TABLE tb_category (
     id BIGINT,
     name VARCHAR(255),
     PRIMARY KEY (id)
 );

 ============================================================================
*/