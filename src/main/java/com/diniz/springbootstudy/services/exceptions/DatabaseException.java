package com.diniz.springbootstudy.services.exceptions;

import java.io.Serial;

// ============================================================================
// SERVICE EXCEPTION LAYER - DATABASE INTEGRITY EXCEPTION
// ============================================================================
// Purpose:
// Custom unchecked exception (RuntimeException) thrown when a database constraint
// is violated (e.g., Foreign Key constraint failure when attempting to delete a record
// that is referenced by other tables/entities).
//
// Advantages:
// - Shields the API from raw Hibernate/JDBC database internal error messages.
// - Allows the Controller Advice layer to catch data integrity failures
//   and translate them into HTTP 400 Bad Request responses.
// ============================================================================

/**
 * Custom exception thrown when a database integrity violation occurs (e.g., Foreign Key constraint).
 */
public class DatabaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DatabaseException with a detailed message.
     *
     * @param msg The error message detailing the database violation.
     */
    public DatabaseException(String msg) {
        super(msg);
    }
}