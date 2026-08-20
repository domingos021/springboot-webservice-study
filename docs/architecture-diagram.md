# 🏛️ Arquitetura do Sistema - Spring Boot E-Commerce Study

Documento de especificação técnica e arquitetura de software da aplicação backend Spring Boot.

---

## 1. Visão Geral da Arquitetura em Camadas (Layered Architecture)

O sistema adota uma arquitetura em camadas tradicional com separação clara de responsabilidades:

```mermaid
graph TD
    Client[Cliente / Postman / Frontend] -->|HTTP REST Requests| Controller[Controller Layer<br/>@RestController]
    Controller -->|DTOs| Service[Service Layer<br/>@Service]
    Service -->|JPA Entities| Repository[Repository Layer<br/>@Repository]
    Repository -->|Spring Data JPA / SQL| Database[(Database H2 / MySQL)]

    subgraph Boundaries
        DTO[DTO Layer<br/>Field Filtering & API Contracts]
    end