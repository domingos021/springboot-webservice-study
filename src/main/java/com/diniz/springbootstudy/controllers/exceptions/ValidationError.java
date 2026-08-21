package com.diniz.springbootstudy.controllers.exceptions;

import java.io.Serial;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// ============================================================================
// VALIDATION ERROR PAYLOAD LAYER (EXTENDS STANDARD ERROR)
// ============================================================================
// Purpose:
// Specializes StandardError to include a collection of specific field errors
// when Bean Validation (@Valid) fails on incoming DTOs.
// ============================================================================

/**
 * Extended error payload carrying field-specific validation failure details.
 */
public class ValidationError extends StandardError {

    @Serial
    private static final long serialVersionUID = 1L;

    private final List<FieldError> errors = new ArrayList<>();

    public ValidationError() {
        super();
    }

    public ValidationError(Instant timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void addError(String fieldName, String message) {
        errors.add(new FieldError(fieldName, message));
    }
}