package com.diniz.springbootstudy.services.exceptions;

import java.io.Serial;

// ============================================================================
// CUSTOM SERVICE EXCEPTION: FORBIDDEN ACCESS (HTTP 403)
// ============================================================================
// Thrown when an authenticated user attempts to perform an operation
// or access a resource for which they lack necessary domain permissions
// (e.g., a CLIENT attempting to inspect another user's order).
// ============================================================================

/**
 * Unchecked exception thrown by the Service Layer when domain authorization rules
 * or resource ownership constraints are violated.
 */
public class ForbiddenException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ForbiddenException with the specified detailed message.
     *
     * @param msg Descriptive explanation of the access violation.
     */
    public ForbiddenException(String msg) {
        super(msg);
    }
}
