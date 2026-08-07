package com.diniz.springbootstudy.resources;
//rodar o projeto no terminal:   mvn spring-boot:run
// REST Controller
// Handles HTTP requests and returns responses


// Service Layer
// Contains the business rules and application logic

// Entity Layer
// Represents the database tables and domain objects


// DTO Layer (Data Transfer Object)
// Used to transfer data between application layers


/*
* Data Access Layer (Repository)
* Responsible for database communication and data persistence
*/
import com.diniz.springbootstudy.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller: Resource Layer
 * Responsible for exposing the HTTP endpoints related to Users.
 */
@RestController
@RequestMapping(value = "/users")
public class UserResource {

    // Test endpoint: GET http://localhost:8080/users
    // mvn spring-boot:run
    @GetMapping
    public ResponseEntity<User> findAll() {
        User u = new User(1L, "backymel@gmail.com", "Domingos", "61984615325", "1234");
        return ResponseEntity.ok().body(u);
    }
}
