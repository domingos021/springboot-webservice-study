package com.diniz.springbootstudy.repositories;

import com.diniz.springbootstudy.entities.OrderItem;
import com.diniz.springbootstudy.entities.pk.OrderItemPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ============================================================================
// DATA ACCESS LAYER ARCHITECTURE (Spring Data JPA)
// ============================================================================
// HTTP Request
//       │
//       ▼
// OrderItemResource (@RestController)
//       │
//       ▼
// OrderItemService (@Service)
//       │
//       ▼
// OrderItemRepository (@Repository)   ◄── Current interface
//       │
//       ▼
// Database
//
// Responsibilities:
// - Encapsulates data persistence and retrieval operations for OrderItem records.
// - Provides out-of-the-box CRUD operations via Spring Data JPA.
// - Acts as an abstraction layer between business logic (Services)
//   and Database (JPA/Hibernate).
// ============================================================================

/**
 * Repository / Data Access Object (DAO) interface for the {@link OrderItem} entity.
 *
 * Extending JpaRepository automatically provides full CRUD capabilities
 * and pagination without needing to write SQL or explicit implementation classes.
 */
@Repository
// Indicates a Data Access component. Optional when extending JpaRepository,
// but recommended for semantic clarity.
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk> {

    /*
     * JpaRepository Generic Parameters:
     * 1. OrderItem   -> Domain Entity class mapped to the database table.
     * 2. OrderItemPk -> Type of the Composite Primary Key (@EmbeddedId) defined inside the OrderItem entity.
     *
     * Standard inherited methods provided automatically by Spring Data JPA:
     *
     * - findAll()
     *      -> Executes: SELECT * FROM tb_order_item
     *
     * - findById(OrderItemPk id)
     *      -> Executes: SELECT * FROM tb_order_item WHERE order_id = ? AND product_id = ?
     *
     * - save(OrderItem orderItem)
     *      -> Executes: INSERT or UPDATE
     *
     * - deleteById(OrderItemPk id)
     *      -> Executes: DELETE FROM tb_order_item WHERE order_id = ? AND product_id = ?
     *
     * - count()
     *      -> Executes: SELECT COUNT(*) FROM tb_order_item
     *
     * Custom Query Methods (Derived Queries / @Query) can be declared here
     * if needed.
     *
     * Example:
     * Optional<OrderItem> findById(OrderItemPk id);
     */
}

/*
============================================================================
SPRING DATA JPA AUTOMATIC IMPLEMENTATION
============================================================================
Spring Data JPA automatically generates a proxy implementation class
for this interface at runtime.

You do NOT need to write a class implementing OrderItemRepository manually.

Bean Creation & Lifecycle:

1. ApplicationContext starts up.

2. Spring Data scans interfaces extending JpaRepository.

3. Spring generates a dynamic proxy implementation class in memory.

4. Registers it as a Spring Bean, ready for dependency injection
   into OrderItemService.

The generated implementation already knows how to communicate with
Hibernate and execute SQL statements against the 'tb_order_item' table.
============================================================================

 */