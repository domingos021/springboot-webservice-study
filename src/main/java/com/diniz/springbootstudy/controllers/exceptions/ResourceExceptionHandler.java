package com.diniz.springbootstudy.controllers.exceptions;

import com.diniz.springbootstudy.services.exceptions.DatabaseException;
import com.diniz.springbootstudy.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

// ============================================================================
// GLOBAL EXCEPTION HANDLING LAYER (@ControllerAdvice) acts on responses
// ============================================================================
// Purpose:
// Intercepts exceptions thrown anywhere in the Controller layer and maps them
// to structured HTTP responses with proper status codes.
//
// Advantages:
// - Centralizes exception handling in a single place.
// - Keeps Controllers clean and focused strictly on HTTP request handling.
// - Guarantees consistent error JSON responses across the entire REST API.
// ============================================================================

/**
 * Global exception handler that intercepts exceptions across all Controllers.
 */
@ControllerAdvice // Intercepts exceptions thrown by @RestController classes.
public class ResourceExceptionHandler {

    // ========================================================================
    // EXCEPTION HANDLER METHOD: ResourceNotFoundException (HTTP 404)
    // ========================================================================
    // Note on Usage (Reflection / Framework Invocation):
    // IDEs may mark this method as "unused" or "0 usages" because it is never
    // called directly in the codebase. Instead, Spring Framework intercepts
    // exceptions globally via @ControllerAdvice and dynamically invokes this
    // method using Reflection at runtime whenever a ResourceNotFoundException is thrown.
    //
    // Runtime Execution Flow Example:
    //  GET /users/999
    //        │
    //        ▼
    //  UserController ──► UserService ──► throw new ResourceNotFoundException(999)
    //                                                  │
    //                                                  ▼ (Spring intercepta em runtime)
    //                                     ResourceExceptionHandler
    //                                        └── resourceNotFound(...) ──► Retorna 404 JSON
    // ========================================================================

    /*
    ==============================================================================
    REQUEST & EXCEPTION HANDLING FLOW (ResourceNotFoundException)
    ==============================================================================

    1º PASSO - REQUEST FLOW:

    • Client (Postman/Browser) envia uma requisição HTTP (ex: GET /users/999).
    • Controller recebe a requisição e delega o processamento ao Service.
    • Service solicita a busca ao Repository.
    • Repository consulta o Database utilizando JPA/Hibernate.

    ┌── IF (Resource Found):
    │
    │   • Database retorna o registro ao Repository.
    │   • Repository retorna a Entity ao Service.
    │   • Service converte a Entity em UserDTO.
    │   • Controller retorna ResponseEntity.ok(dto).
    │   • Client recebe HTTP 200 (OK).
    │
    └── ELSE (Resource Not Found):
        • Repository retorna Optional.empty().
        • Service detecta que o recurso não existe.
        • Service lança:
              throw new ResourceNotFoundException(id);
        • Spring Framework intercepta a exceção.
        • Spring encaminha a exceção para o @ControllerAdvice
          (ResourceExceptionHandler -> resourceNotFound()).
    */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {

        String error = "Resource not found";
        HttpStatus status = HttpStatus.NOT_FOUND; // 404 NOT FOUND

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    // ========================================================================
    // EXCEPTION HANDLER METHOD: DatabaseException (HTTP 400)
    // ========================================================================
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {

        String error = "Database error";
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400 BAD REQUEST

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}

/*
 ============================================================================
 GLOBAL EXCEPTION HANDLING ARCHITECTURE
 ============================================================================

 Exception Thrown               Caught By Method         HTTP Status Code
 -----------------------------  -----------------------  ----------------
 ResourceNotFoundException      resourceNotFound(...)    404 NOT FOUND
 DatabaseException              database(...)            400 BAD REQUEST
 ============================================================================
*/

        /*
        Client => postman/webnavigator
           │
           ▼
        GET /users/999
           │
           ▼
        UserController
           │
           ▼
        UserService
           │
           ▼
        throw new ResourceNotFoundException("Id not found 999")
           │
           ▼
        Spring captura a exceção
           │
           ▼
        @ResourceExceptionHandler
        resourceNotFound(...)
           │
           ▼
        cria um StandardError
           │
           ▼
        return ResponseEntity<StandardError>
           │
           ▼
        HTTP 404 + JSON
         */