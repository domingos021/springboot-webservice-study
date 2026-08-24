package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.dto.ProductDTO;
import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.entities.Product;
import com.diniz.springbootstudy.mappers.ProductMapper;
import com.diniz.springbootstudy.repositories.CategoryRepository;
import com.diniz.springbootstudy.repositories.ProductRepository;
import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================================
// SERVICE LAYER ARCHITECTURE
// ============================================================================
// HTTP Request
//       │
//       ▼
// ProductController (@RestController)
//       │
//       ▼
// ProductService (@Service)   ◄── Current class
//       │
//       ▼
// ProductRepository (@Repository)
//       │
//       ▼
// Database
//
// Responsibilities:
// - Contains business rules and application logic for Product.
// - Receives requests/data from the Controller layer.
// - Uses the Repository layer to communicate with the database.
// - Delegates DTO/Entity transformations to ProductMapper.
// - Handles persistence exceptions and database integrity constraints.
// ============================================================================

@Service
public class ProductService {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository,
                          CategoryRepository categoryRepository,
                          ProductMapper mapper) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        List<Product> list = repository.findAll();

        return list.stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return mapper.toDTO(entity);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = mapper.toEntity(dto);

        copyCategoryAssociations(dto, entity);

        entity = repository.save(entity);

        return mapper.toDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);

            mapper.copyDtoToEntity(dto, entity);
            copyCategoryAssociations(dto, entity);

            entity = repository.save(entity);

            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Helper method to map Category entities from CategoryDTO IDs and attach them to the Product entity.
     */
    private void copyCategoryAssociations(ProductDTO dto, Product entity) {
        if (dto.getCategories() != null && !dto.getCategories().isEmpty()) {
            entity.getCategories().clear();
            for (CategoryDTO catDto : dto.getCategories()) {
                Category category = categoryRepository.getReferenceById(catDto.getId());
                entity.getCategories().add(category);
            }
        }
    }
}