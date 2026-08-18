package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.dto.ProductDTO;
import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.entities.Product;
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
// - Handles persistence exceptions and database integrity constraints.
// - Converts Product entities to ProductDTO before returning data
//   back to the Controller layer.
// ============================================================================


@Service
public class ProductService {


    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final ProductRepository repository;
    // Handles database operations through Spring Data JPA.

    private final CategoryRepository categoryRepository;
    // Handles category entity references for relationships.


    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }


    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================


    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {

        List<Product> list = repository.findAll();

        return list.stream()
                .map(ProductDTO::new)
                .toList();
    }


    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {

        Product entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return new ProductDTO(entity);
    }


    @Transactional
    public ProductDTO insert(ProductDTO dto) {

        Product entity = new Product();

        copyDtoToEntity(dto, entity);

        entity = repository.save(entity);

        return new ProductDTO(entity);
    }


    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {

        try {

            Product entity = repository.getReferenceById(id);

            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);

            return new ProductDTO(entity);

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
     * Copies the editable fields and association references from the DTO to the entity.
     */
    private void copyDtoToEntity(ProductDTO dto, Product entity) {

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();

        for (CategoryDTO catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }
}