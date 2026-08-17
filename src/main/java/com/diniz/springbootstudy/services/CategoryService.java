package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.CategoryDTO;
import com.diniz.springbootstudy.entities.Category;
import com.diniz.springbootstudy.repositories.CategoryRepository;
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
// CategoryController (@RestController)
//       │
//       ▼
// CategoryService (@Service)   ◄── Current class
//       │
//       ▼
// CategoryRepository (@Repository)
//       │
//       ▼
// Database
//
// Responsibilities:
// - Contains business rules and application logic for Category.
// - Receives requests/data from the Controller layer.
// - Uses the Repository layer to communicate with the database.
// - Handles persistence exceptions and database integrity constraints.
// - Converts Category entities to CategoryDTO before returning data
//   back to the Controller layer.
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 *
 * Service indicates that this class holds business logic.
 * Spring automatically manages its lifecycle, allowing it to be injected
 * into controllers or other services.
 */
@Service
public class CategoryService {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final CategoryRepository repository; // Handles database operations.

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {

        List<Category> list = repository.findAll();

        return list.stream()
                .map(CategoryDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {

        Category entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {

        Category entity = new Category();

        entity.setName(dto.getName());

        entity = repository.save(entity);

        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {

        try {

            Category entity = repository.getReferenceById(id);

            updateData(entity, dto);

            entity = repository.save(entity);

            return new CategoryDTO(entity);

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
     * Copies the editable fields from the DTO to the entity.
     */
    private void updateData(Category entity, CategoryDTO dto) {

        entity.setName(dto.getName());
    }
}