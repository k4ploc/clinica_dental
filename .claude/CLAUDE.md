# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5.5 REST API for dental clinic management. Java 21, PostgreSQL 18.1, JWT authentication.

## Common Commands

### Build & Run
```bash
mvn clean compile                    # Compile project
mvn spring-boot:run                  # Run locally (requires DB)
mvn clean package -DskipTests        # Build JAR
```

### Testing
```bash
mvn test                             # Run all tests
mvn test -Dtest=DentistaServiceTest  # Run single test class
mvn test -Dtest=DentistaServiceTest#testFindById  # Run single test method
```

### Docker (Preferred)
```bash
docker-compose up -d                 # Start app + PostgreSQL
docker-compose down                  # Stop services
docker-compose logs -f app           # View app logs
docker-compose down -v               # Stop + delete volumes
```

### Makefile Shortcuts
```bash
make up       # Start dev environment
make down     # Stop services
make logs     # View all logs
make test     # Run tests in Docker
make rebuild  # Clean rebuild
make health   # Check service health
```

## Architecture

### Layer Structure
```
Controller → Service → Repository → Entity
     ↓          ↓           ↓
   DTO     Business     JPA/DB
  Request   Logic      Operations
  Response
```

### Key Packages
- `config/` - Security (JWT filter, SecurityConfig), CORS, OpenAPI
- `controller/` - REST endpoints: AuthController, DentistaController, PacienteController, CitaController, AdminController
- `service/` - Business logic with SLF4J logging
- `repository/` - Spring Data JPA with custom queries
- `model/` - JPA entities with Lombok
- `model/dto/` - Request/Response DTOs
- `model/enums/` - Especialidad, EstadoCita, EstadoEntidad, Rol
- `errors/` - ResourceNotFoundException, DuplicateException

### Domain Model
```
Usuario (auth) ──── Rol (ADMIN, DENTISTA, RECEPCIONISTA, PACIENTE)

Dentista ←──┬──→ Paciente ←──→ Cita
            │                    │
   Especialidad           EstadoCita
   (enum)              (PROGRAMADA, CONFIRMADA,
                        EN_CURSO, COMPLETADA,
                        CANCELADA)

Dentista/Paciente have EstadoEntidad: ACTIVO, INACTIVO, ELIMINADO (soft delete)
```

### Security Flow
1. `JwtAuthenticationFilter` intercepts requests
2. Extracts token from `Authorization: Bearer <token>` header
3. `JwtService` validates and extracts username
4. `CustomUserDetailsService` loads user from DB
5. `SecurityConfig` enforces role-based access

### Database
- **Flyway migrations**: `src/main/resources/db/migration/V*.sql`
- **Naming**: V1-V9 cover tables, indexes, timestamps, estado columns
- **Pool**: HikariCP with 10 connections max
- **Testing**: H2 in-memory database

## API Patterns

### Pagination (all list endpoints)
```
GET /api/dentista?page=0&size=10&sort=id&direction=asc
```

### Soft Delete
- DELETE endpoints set `estado=ELIMINADO`, not physical delete
- Regular endpoints filter by `estado=ACTIVO`
- Admin endpoints (`/api/admin/*`) see all records and can reactivate

### Endpoints Summary
| Base Path | Entity | Notes |
|-----------|--------|-------|
| `/api/auth` | Auth | login, register, validate (public) |
| `/api/dentista` | Dentista | CRUD, requires auth |
| `/api/pacientes` | Paciente | CRUD, requires auth |
| `/api/citas` | Cita | CRUD + estado changes, date queries |
| `/api/admin` | All | Full access including soft-deleted |

### Request/Response DTOs
- Requests: `DentistaRequest`, `PacienteRequest`, `CitaRequest`, `AuthRequest`, `RegisterRequest`
- Responses: `DentistaResponse`, `PacienteResponse`, `CitaResponse`, `AuthResponse`
- Entities use `@JsonProperty` for field mapping (e.g., `id_dentista` → `idDentista`)

## Configuration

### Environment Variables (required in .env)
```
POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB
SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
JWT_SECRET
```

### Optional
```
JWT_EXPIRATION=86400000 (24h default)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

## Testing Notes

- Tests use H2 in-memory database with `@DataJpaTest` or `@SpringBootTest`
- Controller tests use `@WebMvcTest` with `@MockBean` for services
- Security tests use `@WithMockUser` annotation
- Test files mirror main structure: `service/*Test.java`, `controller/*Test.java`
