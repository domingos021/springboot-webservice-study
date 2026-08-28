package com.diniz.springbootstudy.services;

import com.diniz.springbootstudy.dto.OrderDTO01;
import com.diniz.springbootstudy.entities.Order01;
import com.diniz.springbootstudy.entities.User;
import com.diniz.springbootstudy.mappers.OrderMapper;
import com.diniz.springbootstudy.repositories.OrderRepository01;
import com.diniz.springbootstudy.repositories.UserRepository;
import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ForbiddenException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ============================================================================
// SERVICE LAYER ARCHITECTURE & DATA FLOW
// ============================================================================
//
// [Client Request] ──HTTP──> [OrderController01]
//                                   │
//                              (OrderDTO01)
//                                   ▼
//                            [OrderService01] ──(OrderMapper)──> [DTO <─> Entity]
//                                   │
//                               (Order01)
//                                   ▼
//                          [OrderRepository01] ──JPA/SQL──> [Database]
//
// ============================================================================

/**
 * Service Layer component registered as a Spring Bean.
 * Encapsulates business rules, transaction boundaries, domain security validation,
 * and orchestration between Repositories and Mappers for Order operations.
 */
@Service
public class OrderService01 {

    private final OrderRepository01 repository;
    private final UserRepository userRepository;
    private final OrderMapper mapper;

    /**
     * Dependency injection via constructor.
     */
    public OrderService01(OrderRepository01 repository, UserRepository userRepository, OrderMapper mapper) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    // ========================================================================
    // BUSINESS LOGIC / SERVICE METHODS
    // ========================================================================

    /**
     * Retrieves all system orders from the database (ADMIN RESTRICTED).
     * Uses readOnly = true to optimize Hibernate performance by disabling dirty checking.
     */
    @Transactional(readOnly = true)
    public List<OrderDTO01> findAll() {
        List<Order01> list = repository.findAll(); // Fetches all Order01 entities from the database
        return list.stream() // Opens a stream pipeline to process the query result list
                .map(mapper::toDTO) // Applies the toDTO mapping function to each Order01 entity using a method reference
                .toList(); // Collects the transformed elements into an unmodifiable List of OrderDTO01
    }

    /**
     * Retrieves orders placed exclusively by the currently authenticated user (CLIENT).
     */
    @Transactional(readOnly = true)
    public List<OrderDTO01> findMyOrders() {
        User authenticatedUser = getAuthenticatedUser();
        List<Order01> list = repository.findByClientId(authenticatedUser.getId());
        return list.stream()
                .map(mapper::toDTO)
                .toList();
    }

    /**
     * Finds an order by its unique identifier.
     * Enforces real-world ownership validation:
     * - ADMIN can inspect any order.
     * - CLIENT can inspect ONLY their own order.
     *
     * Throws ResourceNotFoundException if the record is missing.
     * Throws ForbiddenException if a CLIENT attempts to access another user's order.
     */
    @Transactional(readOnly = true)
    public OrderDTO01 findById(Long id) {
        /*
         * Queries the database for an Order01 entity by its ID.
         * If present, returns the entity; otherwise, throws a ResourceNotFoundException.
         */
        Order01 entity = repository.findById(id) // Fetches ONE Order01 entity from database by Id
                .orElseThrow(() -> new ResourceNotFoundException(id));

        /*
         * Real-World Access Control Rule:
         * Verifies if the authenticated user is the owner of the order or has ADMIN privileges.
         */
        User authenticatedUser = getAuthenticatedUser();
        validateOrderOwnershipOrAdmin(entity, authenticatedUser);

        /*
         * Maps the retrieved entity to OrderDTO01 and returns it to the controller layer.
         */
        return mapper.toDTO(entity);
    }

    /**
     * Creates and persists a new order in the database attached to the authenticated client.
     */
    @Transactional
    public OrderDTO01 insert(OrderDTO01 dto) {
        /*
         * Converts the input OrderDTO01 into an Order01 JPA entity.
         */
        Order01 entity = mapper.toEntity(dto);

        /*
         * Attaches the currently authenticated user as the client owner of the order.
         */
        User authenticatedUser = getAuthenticatedUser();
        entity.setClient(authenticatedUser);

        /*
         * Persists the newly created Order01 entity into the database.
         */
        entity = repository.save(entity);

        /*
         * Maps the persisted entity (now with generated ID and timestamps) back to an OrderDTO01.
         */
        return mapper.toDTO(entity);
    }

    /**
     * Updates an existing order by its ID with new data provided in the DTO.
     * Uses getReferenceById to obtain a proxy instance without hitting the DB immediately.
     */
    @Transactional
    public OrderDTO01 update(Long id, OrderDTO01 dto) {
        try {
            /*
             * Obtains a lazy-loaded JPA entity proxy for performance optimization.
             */
            Order01 entity = repository.getReferenceById(id);

            /*
             * Validates access permissions before updating.
             */
            User authenticatedUser = getAuthenticatedUser();
            validateOrderOwnershipOrAdmin(entity, authenticatedUser);

            /*
             * Copies updatable scalar attributes and attaches the client reference.
             */
            mapper.copyDtoToEntity(dto, entity);
            attachClient(dto, entity);

            /*
             * Flushes the updated entity state to the database.
             */
            entity = repository.save(entity);

            /*
             * Maps the updated entity state back to OrderDTO01.
             */
            return mapper.toDTO(entity);

        } catch (EntityNotFoundException e) {
            /*
             * Translates JPA EntityNotFoundException into application-specific ResourceNotFoundException.
             */
            throw new ResourceNotFoundException(id);
        }
    }

    /**
     * Deletes an order record by its ID.
     * Handles non-existing resources and relational integrity constraint violations.
     */
    @Transactional
    public void delete(Long id) {
        /*
         * Verifies existence before attempting deletion to throw appropriate 404 exception.
         */
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            /*
             * Translates database constraint violations into a domain-specific DatabaseException.
             */
            throw new DatabaseException(e.getMessage());
        }
    }

    // ========================================================================
    // PRIVATE HELPER & DOMAIN SECURITY METHODS
    // ========================================================================

    /**
     * Safely extracts the currently authenticated User entity from Spring Security Context.
     * Supports both direct domain User instances and Username/UserDetails principals.
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Access denied: No authenticated user context found.");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + username));
    }

    /**
     * Domain Security Guard Clause:
     * Ensures only the owner of the order or an ADMIN user can access order details.
     */
    private void validateOrderOwnershipOrAdmin(Order01 order, User authenticatedUser) {
        boolean isAdmin = authenticatedUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = order.getClient() != null && order.getClient().getId().equals(authenticatedUser.getId());

        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("Access denied: You do not have permission to access this order.");
        }
    }

    /**
     * Helper method to attach a lazy-loaded User proxy reference to the target Order entity.
     * Avoids unnecessary SELECT queries by delegating association management to JPA proxy handling.
     */
    private void attachClient(OrderDTO01 dto, Order01 entity) {
        if (dto.getClient() != null && dto.getClient().getId() != null) {
            User clientEntity = userRepository.getReferenceById(dto.getClient().getId());
            entity.setClient(clientEntity);
        }
    }
}