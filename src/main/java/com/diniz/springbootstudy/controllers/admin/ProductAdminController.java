package com.diniz.springbootstudy.controllers.admin;

import com.diniz.springbootstudy.dto.admin.ProductAdminDTO;
import com.diniz.springbootstudy.services.admin.ProductAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ============================================================================
// ADMIN REST CONTROLLER - BACKOFFICE ENDPOINTS
// ============================================================================
// Base Path: /admin/products
// Target Audience: Internal Administrators, Inventory Managers, Analytics Tools.
//
// Key Responsibilities:
// - Exposes administrative product reports.
// - Provides sales performance indicators per product.
// - Isolates backoffice features from the public storefront API (/products).
// ============================================================================

/**
 * REST Controller exposing Administrative endpoints for Product Analytics & Management.
 */
@RestController
@RequestMapping(value = "/admin/products")
public class ProductAdminController {

    private final ProductAdminService adminService;

    public ProductAdminController(ProductAdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Endpoint: Find All Products with Administrative KPIs
     * GET /admin/products
     */
    @GetMapping
    public ResponseEntity<List<ProductAdminDTO>> findAllAdmin() {
        List<ProductAdminDTO> list = adminService.findAllAdmin();
        return ResponseEntity.ok().body(list);
    }

    /**
     * Endpoint: Find Product by ID with Sales Analytics and Order History
     * GET /admin/products/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductAdminDTO> findByIdAdmin(@PathVariable Long id) {
        ProductAdminDTO dto = adminService.findByIdAdmin(id);
        return ResponseEntity.ok().body(dto);
    }
}