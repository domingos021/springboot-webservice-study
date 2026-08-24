package com.diniz.springbootstudy.services.admin;

import com.diniz.springbootstudy.dto.admin.ProductAdminDTO;
import com.diniz.springbootstudy.entities.Product;
import com.diniz.springbootstudy.mappers.CategoryMapper;
import com.diniz.springbootstudy.repositories.ProductRepository;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================================
// ADMIN SERVICE LAYER - BACKOFFICE BUSINESS LOGIC
// ============================================================================
// Purpose:
// Encapsulates administrative logic, analytics processing, and management reporting.
// Separated from ProductService to allow independent scaling and access control.
// ============================================================================

/**
 * Service Layer component dedicated to Backoffice / Admin operations.
 */
@Service
public class ProductAdminService {

    private final ProductRepository repository;
    private final CategoryMapper.ProductAdminMapper mapper;

    public ProductAdminService(ProductRepository repository, CategoryMapper.ProductAdminMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Retrieves all products enriched with administrative KPIs and sales reports.
     *
     * @return List of ProductAdminDTO.
     */
    @Transactional(readOnly = true)
    public List<ProductAdminDTO> findAllAdmin() {
        List<Product> list = repository.findAll();
        return list.stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Retrieves a single product by ID with full administrative analytics.
     *
     * @param id Product ID.
     * @return ProductAdminDTO.
     */
    @Transactional(readOnly = true)
    public ProductAdminDTO findByIdAdmin(Long id) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return mapper.toDTO(entity);
    }
}