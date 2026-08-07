package com.diniz.springbootstudy.repositories;

import com.diniz.springbootstudy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object (DAO) / Repository interface for the {@link User} entity.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Encapsulates data persistence and retrieval operations for User records in the database.</li>
 *   <li>Provides out-of-the-box CRUD (Create, Read, Update, Delete) operations by extending Spring Data JPA's {@link JpaRepository}.</li>
 *   <li>Acts as an abstraction layer between the business logic (Services) and the database access layer (JPA/Hibernate).</li>
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /*
    * JpaRepository<User, Long>
    * type of entity -> User
    *  type of id -> Long
    */
    // Custom query methods (e.g., findByEmail) can be defined here if needed.
}