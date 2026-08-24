package com.diniz.springbootstudy.controllers;

import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.services.OrderService01;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// ============================================================================
// ARCHITECTURAL OVERVIEW OF LAYERS IN A SPRING BOOT PROJECT
// ============================================================================
// REST Controller -> Service Layer -> Repository -> Entity -> DTO
// ============================================================================

/**
 * REST Controller: Resource Layer
 *
 * Responsible for exposing the HTTP endpoints related to Order01.
 */
@RestController
@RequestMapping(value = "/orders01")
public class OrderController01 {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final OrderService01 service;

    public OrderController01(OrderService01 service) {
        this.service = service;
    }

    // =========================================================================
    // ENDPOINT: Find All Order01 Records
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders01
    // =========================================================================

    @GetMapping
    public ResponseEntity<List<OrderDTO01>> findAll() {
        List<OrderDTO01> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    // =========================================================================
    // ENDPOINT: Find Order01 by ID
    // HTTP Method: GET
    // URL Example: http://localhost:8080/orders01/1
    // =========================================================================

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO01> findById(@PathVariable Long id) {
        OrderDTO01 dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    // =========================================================================
    // ENDPOINT: Insert Order01
    // HTTP Method: POST
    // URL Example: http://localhost:8080/orders01
    // =========================================================================

    @PostMapping
    public ResponseEntity<OrderDTO01> insert(@Valid @RequestBody OrderDTO01 dto) {
        dto = service.insert(dto);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    // =========================================================================
    // ENDPOINT: Update Order01
    // HTTP Method: PUT
    // URL Example: http://localhost:8080/orders01/1
    // =========================================================================

    @PutMapping("/{id}")
    public ResponseEntity<OrderDTO01> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderDTO01 dto) {

        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

    // =========================================================================
    // ENDPOINT: Delete Order01
    // HTTP Method: DELETE
    // URL Example: http://localhost:8080/orders01/1
    // =========================================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}