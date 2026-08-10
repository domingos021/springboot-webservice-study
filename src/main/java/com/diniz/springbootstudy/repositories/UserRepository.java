package com.diniz.springbootstudy.repositories;

import com.diniz.springbootstudy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ============================================================================
// DATA ACCESS LAYER ARCHITECTURE (Spring Data JPA)
// ============================================================================
// HTTP Request
//       │
//       ▼
// UserResource (@RestController)
//       │
//       ▼
// UserService (@Service)
//       │
//       ▼
// UserRepository (@Repository)   ◄── Current interface
//       │
//       ▼
// Database
//
// Responsibilities:
// - Encapsulates data persistence and retrieval operations for User records.
// - Provides out-of-the-box CRUD operations via Spring Data JPA.
// - Acts as an abstraction layer between business logic (Services) and Database (JPA/Hibernate).
// ============================================================================

/**
 * Repository / Data Access Object (DAO) interface for the {@link User} entity.
 *
 * Extending JpaRepository automatically provides full CRUD capabilities and pagination
 * without needing to write SQL or explicit implementation classes.
 */
@Repository // Indicates a Data Access component. Optional when extending JpaRepository, but recommended for semantic clarity.
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * JpaRepository Generic Parameters:
     * 1. User -> Domain Entity class mapped to the database table.
     * 2. Long -> Type of the Primary Key (@Id) defined inside the User entity.
     *
     * Standard inherited methods provided automatically by Spring Data JPA:
     * - findAll()                -> Executa: SELECT * FROM tb_user
     * - findById(Long id)        -> Executa: SELECT * FROM tb_user WHERE id = ?
     * - save(User user)          -> Executa: INSERT ou UPDATE
     * - deleteById(Long id)      -> Executa: DELETE FROM tb_user WHERE id = ?
     * - count()                  -> Executa: SELECT COUNT(*) FROM tb_user
     *
     * Custom Query Methods (Derived Queries / @Query) can be declared here if needed:
     * Example:
     * Optional<User> findByEmail(String email);
     */
}

/*
 ============================================================================
 SPRING DATA JPA AUTOMATIC IMPLEMENTATION
 ============================================================================
 Spring Data JPA automatically generates a proxy implementation class
 for this interface at runtime.

 You do NOT need to write a class implementing UserRepository manually.

 Bean Creation & Lifecycle:
 1. ApplicationContext starts up.
 2. Spring Data scans interfaces extending JpaRepository.
 3. Spring generates a dynamic proxy implementation class in memory.
 4. Registers it as a Spring Bean ready for Constructor Injection in UserService.
 ============================================================================
*/