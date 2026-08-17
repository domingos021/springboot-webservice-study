package com.diniz.springbootstudy.repositories;

import com.diniz.springbootstudy.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ============================================================================
// DATA ACCESS LAYER ARCHITECTURE (Spring Data JPA)
// ============================================================================
// HTTP Request
//       │
//       ▼
// CategoryResource (@RestController)
//       │
//       ▼
// CategoryService (@Service)
//       │
//       ▼
// CategoryRepository (@Repository)   ◄── Current interface
//       │
//       ▼
// Database
//
// Responsibilities:
// - Encapsulates data persistence and retrieval operations for Category records.
// - Provides out-of-the-box CRUD operations via Spring Data JPA.
// - Acts as an abstraction layer between business logic (Services) and Database (JPA/Hibernate).
// ============================================================================

/**
 * Repository / Data Access Object (DAO) interface for the {@link Category} entity.
 *
 * Extending JpaRepository automatically provides full CRUD capabilities and pagination
 * without needing to write SQL or explicit implementation classes.
 */
@Repository // Indicates a Data Access component. Optional when extending JpaRepository, but recommended for semantic clarity.
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /*
     * JpaRepository Generic Parameters:
     * 1. Category -> Domain Entity class mapped to the database table.
     * 2. Long     -> Type of the Primary Key (@Id) defined inside the Category entity.
     *
     * Standard inherited methods provided automatically by Spring Data JPA:
     * - findAll()                 -> Executes: SELECT * FROM tb_category
     * - findById(Long id)         -> Executes: SELECT * FROM tb_category WHERE id = ?
     * - save(Category category)   -> Executes: INSERT or UPDATE
     * - deleteById(Long id)       -> Executes: DELETE FROM tb_category WHERE id = ?
     * - count()                   -> Executes: SELECT COUNT(*) FROM tb_category
     *
     * Custom Query Methods (Derived Queries / @Query) can be declared here if needed:
     * Example:
     * Optional<Category> findByName(String name);
     */
}

/*
 ============================================================================
 SPRING DATA JPA AUTOMATIC IMPLEMENTATION
 ============================================================================
 Spring Data JPA automatically generates a proxy implementation class
 for this interface at runtime.

 You do NOT need to write a class implementing CategoryRepository manually.

 Bean Creation & Lifecycle:
 1. ApplicationContext starts up.
 2. Spring Data scans interfaces extending JpaRepository.
 3. Spring generates a dynamic proxy implementation class in memory.
 4. Registers it as a Spring Bean ready for Constructor Injection in CategoryService.
 ============================================================================
*/