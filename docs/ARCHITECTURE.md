# 🏗️ Arquitectura del Proyecto Clínica

## 📋 Tabla de Contenidos
1. [Patrones Arquitectónicos](#patrones-arquitectónicos)
2. [Capas de la Aplicación](#capas-de-la-aplicación)
3. [Flujo de Datos](#flujo-de-datos)
4. [Componentes Principales](#componentes-principales)
5. [Patrones de Diseño](#patrones-de-diseño)
6. [Relaciones entre Componentes](#relaciones-entre-componentes)

---

## 🎯 Patrones Arquitectónicos

### 1. Arquitectura en Capas (Layered Architecture)

La aplicación sigue el patrón clásico de capas, donde cada capa tiene responsabilidades bien definidas:

```
┌─────────────────────────────────────────────────────┐
│        PRESENTATION LAYER (Controllers)             │
│  ┌──────────────────┐  ┌──────────────────┐         │
│  │ DentistaController│  │PacienteController│        │
│  └──────────────────┘  └──────────────────┘         │
└────────────────────────┬────────────────────────────┘
                         │ (JSON/HTTP)
┌────────────────────────▼────────────────────────────┐
│       BUSINESS LOGIC LAYER (Services)               │
│  ┌──────────────────┐  ┌──────────────────┐         │
│  │ DentistaService  │  │ PacienteService  │         │
│  └──────────────────┘  └──────────────────┘         │
└────────────────────────┬────────────────────────────┘
                         │ (Domain Objects)
┌────────────────────────▼────────────────────────────┐
│      DATA ACCESS LAYER (Repositories)               │
│  ┌──────────────────┐  ┌──────────────────┐         │
│  │DentistaRepository│  │PacienteRepository│        │
│  └──────────────────┘  └──────────────────┘         │
└────────────────────────┬────────────────────────────┘
                         │ (SQL Queries)
┌────────────────────────▼────────────────────────────┐
│      DATABASE LAYER (PostgreSQL)                    │
│  ┌──────────────────┐  ┌──────────────────┐         │
│  │    DENTISTA      │  │    PACIENTE      │         │
│  └──────────────────┘  └──────────────────┘         │
└─────────────────────────────────────────────────────┘
```

### Ventajas:
- ✅ Separación de responsabilidades
- ✅ Fácil de mantener y testear
- ✅ Escalable horizontalmente
- ✅ Reutilización de código

---

## 📚 Capas de la Aplicación

### 1. **Presentation Layer (Capa de Presentación)**

**Ubicación:** `controller/`

**Responsabilidades:**
- Recibir solicitudes HTTP
- Validar parámetros inicialmente
- Delegar a servicios
- Devolver respuestas HTTP

**Componentes:**
```
DentistaController
├── GET /dentista          → getDentistas()
└── POST /dentista         → createDentista()

PacienteController
├── GET /pacientes         → listPacientes()
├── POST /pacientes        → crearPaciente()
├── GET /pacientes/{id}    → obtenerPaciente()
└── DELETE /pacientes/{id} → eliminarPaciente()
```

**Ejemplo:**
```java
@RestController
@RequestMapping("/dentista")
public class DentistaController {
    @GetMapping
    public ResponseEntity<?> getDentistas() {
        var listaDentistas = service.getDentistas();
        return ResponseEntity.ok(listaDentistas);
    }
}
```

---

### 2. **Business Logic Layer (Capa de Lógica de Negocio)**

**Ubicación:** `service/`

**Responsabilidades:**
- Implementar reglas de negocio
- Orquestar operaciones de datos
- Validaciones de negocio
- Transformación de datos (mapeo a DTOs)

**Componentes:**

#### DentistaService
```java
@Service
public class DentistaService {
    ✓ createDentista()      - Crear dentista
    ✓ getDentistas()        - Obtener todos
    ✓ toResponse()          - Mapear a DTO
}
```

#### PacienteService
```java
@Service
public class PacienteService {
    ✓ listarPacientes()     - Listar todos
    ✓ crearPaciente()       - Crear paciente
    ✓ toResponse()          - Mapear a DTO
    ✗ obtenerPaciente()     - Pendiente
    ✗ actualizarPaciente()  - Pendiente
}
```

**Ejemplo de Lógica de Negocio:**
```java
public void crearPaciente(PacienteRequest request) {
    // Validación de negocio
    var existsEmail = repository.existsByEmail(request.email());
    if (existsEmail) {
        throw new DuplicateException("El email ya se registro");
    }
    
    // Verificar que el dentista existe
    Dentista dentista = dentistaRepository.findById(request.idDentista())
        .orElseThrow(() -> new RuntimeException("Dentista no encontrado"));
    
    // Crear paciente
    repository.save(new Paciente(request, dentista));
}
```

---

### 3. **Data Access Layer (Capa de Acceso a Datos)**

**Ubicación:** `repository/`

**Responsabilidades:**
- Abstraer la base de datos
- Ejecutar consultas SQL
- Gestionar transacciones
- Implements Spring Data JPA

**Componentes:**

#### DentistaRepository
```java
@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    // Hereda: findAll(), findById(), save(), delete(), etc.
}
```

#### PacienteRepository
```java
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    ✓ existsByEmail(String email)    - Validación de email único
}
```

**Queries Generadas:**
```sql
-- findAll()
SELECT * FROM paciente;

-- existsByEmail()
SELECT COUNT(*) FROM paciente WHERE email = ?;

-- save()
INSERT INTO paciente (...) VALUES (...);
UPDATE paciente SET ... WHERE id = ?;
```

---

### 4. **Model Layer (Capa de Modelo)**

**Ubicación:** `model/`

**Responsabilidades:**
- Definir entidades JPA
- Definir DTOs
- Mapeo objeto-relacional

#### Entidades JPA
```
Dentista (Entity)
├── id
├── nombre
├── apellido
├── telefono
├── especialidad (Enum)
└── pacientes (List<Paciente>)

Paciente (Entity)
├── id
├── nombre
├── apellido
├── telefono
├── email
└── dentista (Dentista)

Cita (POJO - no persistido)
├── id
├── pacienteId
├── dentistaId
├── fechaHora
└── motivo
```

#### DTOs (Data Transfer Objects)
```
DentistaRequest (Record)
├── nombre: String
├── apellido: String
├── telefono: String
└── especialidad: String

DentistaResponse (Record)
├── id: Long
├── nombre: String
├── apellido: String
├── telefono: String
├── especialidad: String
└── pacientes: List<PacienteResponse>

PacienteRequest (Record)
├── nombre: String
├── apellido: String
├── telefono: String
├── email: String
└── id_dentista: Long

PacienteResponse (Record)
├── id: Long
├── nombre: String
├── apellido: String
├── telefono: String
└── email: String
```

---

### 5. **Configuration & Infrastructure Layer**

**Ubicación:** `config/`

#### GlobalExceptionHandler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    ✓ handleValidationExceptions()   - Errores de validación
    ✓ handleDataIntegrityViolation() - Errores de integridad
}
```

#### SecurityConfig
```java
@Configuration
public class SecurityConfig {
    ✗ CSRF Protection     - Desactivada
    ✗ Authentication      - No implementada
    ✓ Allow All Requests  - Configurado
}
```

---

## 🔄 Flujo de Datos

### Ejemplo: Crear un Dentista

```
1. Cliente HTTP
   │
   └─► POST /dentista
       {
         "nombre": "Juan",
         "apellido": "García",
         "telefono": "3001234567",
         "especialidad": "ORTODONCISTA"
       }

2. DentistaController.createDentista()
   │
   ├─► Recibe DentistaRequest
   ├─► Valida (@Valid)
   │
   └─► DentistaService.createDentista()

3. DentistaService
   │
   ├─► new Dentista(request)  // Transforma request a entidad
   │
   └─► DentistaRepository.save(dentista)

4. DentistaRepository (JPA)
   │
   ├─► Genera SQL INSERT
   ├─► Ejecuta en PostgreSQL
   ├─► Obtiene ID generado
   │
   └─► Retorna Dentista persistido

5. DentistaService.toResponse()
   │
   └─► Transforma Dentista a DentistaResponse

6. DentistaController
   │
   └─► return ResponseEntity.ok(response)

7. Cliente HTTP
   │
   └─► 200 OK
       {
         "id": 1,
         "nombre": "Juan",
         "apellido": "García",
         "telefono": "3001234567",
         "especialidad": "ORTODONCISTA",
         "pacientes": null
       }
```

---

## 🧩 Componentes Principales

### Relaciones entre Componentes

```
┌─────────────────────────────────────────┐
│      DentistaController                 │
│  (Recibe HTTP requests)                 │
└────────────────┬────────────────────────┘
                 │ inyecta
┌────────────────▼────────────────────────┐
│      DentistaService                    │
│  (Lógica de negocio)                    │
└────────────────┬────────────────────────┘
                 │ usa
┌────────────────▼────────────────────────┐
│      DentistaRepository                 │
│  (Acceso a datos)                       │
└────────────────┬────────────────────────┘
                 │ consulta
┌────────────────▼────────────────────────┐
│      PostgreSQL (dentista table)        │
│  (Persistencia)                         │
└─────────────────────────────────────────┘
```

### Inyección de Dependencias

La inyección de dependencias (Dependency Injection) se realiza a través del constructor:

```java
@Service
public class DentistaService {
    private final DentistaRepository repository;
    
    public DentistaService(DentistaRepository repository) {
        this.repository = repository;  // Inyectado por Spring
    }
}
```

---

## 🎨 Patrones de Diseño

### 1. **Repository Pattern**
Define una abstracción para acceder a datos:
```java
public interface DentistaRepository extends JpaRepository<Dentista, Long>
```

### 2. **Dependency Injection**
Inyecta dependencias a través del constructor:
```java
public DentistaService(DentistaRepository repository)
```

### 3. **Data Transfer Object (DTO)**
Separa la representación interna de la respuesta:
```java
public record DentistaResponse(Long id, String nombre, ...) {}
```

### 4. **Service Locator Pattern**
Spring actúa como localizador de servicios centralizando la creación de beans.

### 5. **Strategy Pattern**
Diferente mapeo en toResponse() basado en el tipo de objeto.

---

## 📊 Diagrama de Clases

```
┌──────────────────────────────────────┐
│        <<Entity>>                    │
│         Dentista                     │
├──────────────────────────────────────┤
│ - id: Long                           │
│ - nombre: String                     │
│ - apellido: String                   │
│ - telefono: String                   │
│ - especialidad: Especialidad         │
│ - pacientes: List<Paciente>          │
├──────────────────────────────────────┤
│ + Dentista(request: DentistaRequest) │
└──────────────────────────────────────┘
              ▲
              │ referenciada en
              │
┌──────────────────────────────────────┐
│        <<Entity>>                    │
│         Paciente                     │
├──────────────────────────────────────┤
│ - id: Long                           │
│ - nombre: String                     │
│ - apellido: String                   │
│ - telefono: String                   │
│ - email: String                      │
│ - dentista: Dentista                 │
├──────────────────────────────────────┤
│ + Paciente(req, dentista)            │
└──────────────────────────────────────┘
```

---

## 🔌 Interfaces Clave

### Repository Interfaces (Spring Data JPA)

```java
@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    // Métodos heredados de JpaRepository
    List<Dentista> findAll();
    Optional<Dentista> findById(Long id);
    Dentista save(Dentista entity);
    void deleteById(Long id);
    boolean existsById(Long id);
}

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByEmail(String email);
}
```

---

## 🌐 Integración con Spring Boot

### Configuración Automática
Spring Boot detecta automáticamente:
- `@Entity` - Entidades JPA
- `@Repository` - Repositories
- `@Service` - Servicios
- `@RestController` - Controladores REST

### Ciclo de Vida de Bean Spring

```
1. Creación de contenedor Spring
   └─► Detecta clases anotadas

2. Registro de Beans
   ├─► DentistaRepository
   ├─► PacienteRepository
   ├─► DentistaService
   ├─► PacienteService
   ├─► DentistaController
   └─► PacienteController

3. Inyección de Dependencias
   ├─► DentistaService ← DentistaRepository
   ├─► PacienteService ← PacienteRepository, DentistaRepository
   ├─► DentistaController ← DentistaService
   └─► PacienteController ← PacienteService

4. Aplicación Lista
   └─► Puede procesar requests
```

---

## 📈 Escalabilidad

### Horizontal Scaling
Para escalar horizontalmente (múltiples instancias):
- Usar load balancer (Nginx, HAProxy)
- Compartir base de datos (PostgreSQL replicado)
- Cache distribuido (Redis)

### Vertical Scaling
Para escalar verticalmente (más recursos):
- Aumentar memoria JVM (-Xms, -Xmx)
- Optimizar queries y índices
- Connection pooling (HikariCP)

---

## 🔒 Consideraciones de Seguridad

1. **Validación de Entrada**
   - `@Valid` en DTOs
   - `@NotBlank`, `@NotNull` en campos

2. **Manejo de Excepciones**
   - `GlobalExceptionHandler` centraliza errores
   - Evita exposición de stack traces

3. **Separación de Responsabilidades**
   - Controllers no acceden directamente a BD
   - Services contienen lógica de validación

4. **Inyección de Dependencias**
   - Facilita testing y cambio de implementaciones

---

**Última actualización:** Diciembre 2025

