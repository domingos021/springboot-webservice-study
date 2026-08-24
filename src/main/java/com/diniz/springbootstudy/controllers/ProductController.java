package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.ProductDTO;
import com.diniz.springbootstudy.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
 * Responsible for exposing the HTTP endpoints related to Products.
 */
@RestController
@RequestMapping(value = "/products")
public class ProductController {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // ========================================================================
    // ENDPOINT: Find All Products
    // HTTP Method: GET
    // URL Example: http://localhost:8080/products
    // ========================================================================

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll() {
        List<ProductDTO> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    // ========================================================================
    // ENDPOINT: Find Product by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/products/1
    // ========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
        ProductDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // ========================================================================
    // ENDPOINT: Insert Product
    // HTTP Method: POST
    // URL Example: http://localhost:8080/products
    // ========================================================================

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO dto) {
        dto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    // ========================================================================
    // ENDPOINT: Update Product
    // HTTP Method: PUT
    // URL Example: http://localhost:8080/products/1
    // ========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {

        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    // ========================================================================
    // ENDPOINT: Delete Product
    // HTTP Method: DELETE
    // URL Example: http://localhost:8080/products/1
    // ========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}