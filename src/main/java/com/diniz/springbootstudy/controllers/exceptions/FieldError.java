package com.diniz.springbootstudy.controllers.exceptions;

import java.io.Serial;
import java.io.Serializable;

// ============================================================================
// FIELD ERROR PAYLOAD LAYER
// ============================================================================
// Purpose:
// Represents an individual validation error detail for a specific JSON field.
// Used inside ValidationError to list all invalid attributes in a request payload.
// ============================================================================

/**
 * Payload representing a specific field validation error name and message.
 */
public class FieldError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String message;

    public FieldError() {
    }

    public FieldError(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    // ========================================================================
    // GETTERS AND SETTERS
    // ========================================================================

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}