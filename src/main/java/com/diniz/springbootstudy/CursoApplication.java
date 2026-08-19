package com.diniz.springbootstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ============================================================================
// APPLICATION ENTRY POINT & OVERALL SYSTEM ARCHITECTURE
// ============================================================================
// Purpose:
// The main bootstrapper class for launching the Spring Boot application context.
//
// Annotations:
// @SpringBootApplication enables:
// 1. @EnableAutoConfiguration: Automatic Spring configuration based on classpath.
// 2. @ComponentScan: Scans packages for components, services, controllers, and repositories.
// 3. @Configuration: Allows registering extra beans in the application context.
// ============================================================================

/**
 * Main application entry point for launching the Spring Boot backend.
 *
 * Useful Commands:
 * - Run via terminal: mvn spring-boot:run
 */
@SpringBootApplication
public class CursoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CursoApplication.class, args);
	}
}

/*
 ============================================================================
 HIGH-LEVEL SYSTEM ARCHITECTURE WITH DTO PIPELINE
 ============================================================================

  Cliente (Frontend / Mobile / Postman)
         │
         │ 1. HTTP Request (JSON) / 8. HTTP Response (Filtered JSON)
         ▼
  [ REST Controller ]  ◄── Deals only with DTOs (Contracts)
         │
         │ 2. Passes DTO or ID / 7. Receives DTO (Safe Payload)
         ▼
  [ Service Layer ]    ◄── Converts Entity ──► DTO (Filters out sensitive fields like password)
         │
         │ 3. Works with JPA Entities / 6. Returns JPA Entity
         ▼
  [ Repository Layer ] ◄── Spring Data JPA Interfaces
         │
         │ 4. ORM Translation / 5. Entity Mapping
         ▼
  [ JPA / Hibernate ]
         │
         ▼
  [ Database ] (H2 / PostgreSQL / MySQL)

 ============================================================================
 DETAILED STEP-BY-STEP DATA FLOW (REQUEST & RESPONSE)
 ============================================================================

 REQUISIÇÃO (FLUXO DE IDA):
 Client ──► Sends HTTP GET /users/1
   │
   ▼
 Controller ──► Calls service.findById(1L)
   │
   ▼
 Service ──► Calls repository.findById(1L)
   │
   ▼
 Repository / Database ──► Executes SELECT * FROM tb_user WHERE id = 1

 ----------------------------------------------------------------------------

 RESPOSTA (FLUXO DE VOLTA COM ATUAÇÃO DO DTO):
 Database ──► Returns User Entity (id, name, email, phone, password)
   │
   ▼
 Repository ──► Passes Optional<User> to Service
   │
   ▼
 Service ──► Applies DTO Filter: new UserDTO(entity)
   │         [ Atributo 'password' é descartado aqui ]
   ▼
 Controller ──► Receives filtered UserDTO (id, name, email, phone)
   │
   ▼
 Client ◄── Receives 200 OK with clean JSON (No password exposed)
 ============================================================================
*/


//git clone https://github.com/domingos021/springboot-webservice-study .