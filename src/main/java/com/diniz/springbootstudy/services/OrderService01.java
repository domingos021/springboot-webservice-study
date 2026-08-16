package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.order01.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.repositories.OrderRepository01;
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
// OrderController01 (@RestController)
//       │
//       ▼
// OrderService01 (@Service)   ◄── Current class
//       │
//       ▼
// OrderRepository01 (@Repository)
//       │
//       ▼
// Database
//
// Responsibilities:
// - Contains business rules and application logic for Order01.
// - Receives requests/data from the Controller layer.
// - Uses the Repository layer to communicate with the database.
// - Handles persistence exceptions and database integrity constraints.
// - Converts Order01 entities to OrderDTO01 before returning data
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
public class OrderService01 {

    // =========================================================
    // CONSTRUCTOR INJECTION (Recommended Standard)
    // =========================================================

    private final OrderRepository01 repository;

    public OrderService01(OrderRepository01 repository) {
        this.repository = repository;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {

        List<Order01> list = repository.findAll();

        return list.stream()
                .map(OrderDTO01::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO01 findById(Long id) {

        Order01 entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        return new OrderDTO01(entity);
    }

    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {

        Order01 entity = new Order01();

        entity.setMoment(dto.getMoment());
        entity.setClient(dto.getClient());

        entity = repository.save(entity);

        return new OrderDTO01(entity);
    }

    @Transactional
    public OrderDTO01 update(Long id, OrderDTO01 dto) {

        try {

            Order01 entity = repository.getReferenceById(id);

            updateData(entity, dto);

            entity = repository.save(entity);

            return new OrderDTO01(entity);

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
    private void updateData(Order01 entity, OrderDTO01 dto) {

        entity.setMoment(dto.getMoment());
        entity.setClient(dto.getClient());

        // Quando OrderDTO01 possuir o campo orderStatus,
        // basta adicionar:
        //
        // entity.setOrderStatus(dto.getOrderStatus());
    }
}