package com.diniz.springbootstudy.services.exceptions;

import java.io.Serial;

// ============================================================================
// SERVICE EXCEPTION LAYER
// ============================================================================
// Purpose:
// Custom unchecked exception (RuntimeException) thrown by the Service layer
// when a requested database resource is not found.
//
// Advantages:
// - Decouples business logic failure handling from standard Java exceptions.
// - Triggers automatic transaction rollback in Spring (@Transactional).
// - Allows the Controller Advice layer to catch specific error scenarios.
// ============================================================================

/**
 * Custom exception thrown when a resource is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ResourceNotFoundException with a default formatted message.
     *
     * @param id The ID of the missing resource.
     */
    public ResourceNotFoundException(Object id) {
        super("Resource not found. Id: " + id);
    }
}