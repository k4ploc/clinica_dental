# 🚀 GUÍA PRÁCTICA: IMPLEMENTACIÓN DE MEJORAS

**Fecha:** Diciembre 20, 2025  
**Documento:** Ejemplos paso a paso para cada mejora

---

## 📌 ÍNDICE RÁPIDO

1. [Logging con SLF4J](#1-logging-con-slf4j)
2. [Swagger/OpenAPI](#2-swagger-openapi)
3. [Validaciones Avanzadas](#3-validaciones-avanzadas)
4. [Spring Data Specifications](#4-spring-data-specifications)
5. [Auditoría Completa](#5-auditoría-completa)
6. [Rate Limiting](#6-rate-limiting)

---

## 1. LOGGING CON SLF4J

### ✅ Ya incluido en Spring Boot

**Paso 1:** Configurar `application.properties`

```properties
# Logging levels
logging.level.root=INFO
logging.level.com.clinica=DEBUG
logging.level.org.springframework.web=WARN
logging.level.org.hibernate.SQL=DEBUG

# Formato de logs
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# Archivo de logs
logging.file.name=logs/clinica.log
logging.file.max-size=10MB
logging.file.max-history=10
```

**Paso 2:** Inyectar logger en servicios

```java
package com.clinica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DentistaService {
    private static final Logger log = LoggerFactory.getLogger(DentistaService.class);

    @Transactional(readOnly = true)
    public DentistaResponse obtenerDentista(Long id) {
        log.info("Obteniendo dentista con ID: {}", id);
        
        Dentista dentista = repository.findById(id)
            .orElseThrow(() -> {
                log.warn("Dentista no encontrado con ID: {}", id);
                return new ResourceNotFoundException("Dentista", id);
            });
        
        log.debug("Dentista encontrado: {} {}", dentista.getNombre(), dentista.getApellido());
        return toResponse(dentista);
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public DentistaResponse actualizarDentista(Long id, DentistaRequest request) {
        log.info("Actualizando dentista con ID: {}", id);
        
        Dentista dentista = repository.findById(id)
            .orElseThrow(() -> {
                log.error("No se puede actualizar: Dentista no encontrado con ID: {}", id);
                return new ResourceNotFoundException("Dentista", id);
            });

        dentista.setNombre(request.nombre());
        dentista.setApellido(request.apellido());
        // ...
        
        Dentista actualizado = repository.save(dentista);
        log.info("Dentista actualizado exitosamente: {}", actualizado.getId());
        return toResponse(actualizado);
    }

    @Transactional
    @CacheEvict(value = "dentistas", allEntries = true)
    public void eliminarDentista(Long id) {
        log.info("Eliminando dentista con ID: {}", id);
        
        if (!repository.existsById(id)) {
            log.error("No se puede eliminar: Dentista no encontrado con ID: {}", id);
            throw new ResourceNotFoundException("Dentista", id);
        }
        
        repository.deleteById(id);
        log.info("Dentista eliminado exitosamente: {}", id);
    }
}
```

**Paso 3:** Lo mismo en `PacienteService`

```java
// Agregar logger
private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

// Agregar en cada método
log.info("...");
log.debug("...");
log.warn("...");
log.error("...");
```

**Resultado:**
```
2025-12-20 10:15:30 [http-nio-8080-exec-1] INFO  com.clinica.service.DentistaService - Obteniendo dentista con ID: 1
2025-12-20 10:15:30 [http-nio-8080-exec-1] DEBUG com.clinica.service.DentistaService - Dentista encontrado: Juan García
2025-12-20 10:15:31 [http-nio-8080-exec-1] INFO  com.clinica.service.DentistaService - Actualizando dentista con ID: 1
```

---

## 2. SWAGGER/OPENAPI

### Paso 1: Agregar Dependencia

**En `pom.xml`:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

Luego: `mvn clean install`

### Paso 2: Configurar el Controller

```java
package com.clinica.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/dentista")
@Tag(name = "Dentista Management", description = "APIs para gestionar dentistas")
public class DentistaController {

    @GetMapping
    @Operation(
        summary = "Listar dentistas",
        description = "Retorna una página de dentistas con paginación",
        tags = {"Dentista"}
    )
    @ApiResponse(
        responseCode = "200",
        description = "Lista de dentistas obtenida exitosamente",
        content = @Content(schema = @Schema(implementation = Page.class))
    )
    public ResponseEntity<Page<DentistaResponse>> getDentistas(
        @Parameter(description = "Página (0-based), tamaño, ordenamiento")
        Pageable pageable) {
        Page<DentistaResponse> page = service.getDentistasPaginados(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener dentista por ID",
        description = "Retorna un dentista específico por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Dentista encontrado",
            content = @Content(schema = @Schema(implementation = DentistaResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Dentista no encontrado"
        )
    })
    public ResponseEntity<DentistaResponse> obtenerDentista(
        @Parameter(description = "ID del dentista", required = true)
        @PathVariable Long id) {
        DentistaResponse response = service.obtenerDentista(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
        summary = "Crear nuevo dentista",
        description = "Crea un nuevo registro de dentista"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Dentista creado exitosamente"
    )
    public ResponseEntity<DentistaResponse> createDentista(
        @Parameter(description = "Datos del nuevo dentista", required = true)
        @Valid @RequestBody DentistaRequest request) {
        Dentista newDentista = service.createDentista(request);
        DentistaResponse response = new DentistaResponse(
            newDentista.getId(),
            newDentista.getNombre(),
            newDentista.getApellido(),
            newDentista.getTelefono(),
            newDentista.getEspecialidad() != null ? newDentista.getEspecialidad().name() : null,
            null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar dentista",
        description = "Actualiza un dentista existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dentista actualizado"),
        @ApiResponse(responseCode = "404", description = "Dentista no encontrado")
    })
    public ResponseEntity<DentistaResponse> actualizarDentista(
        @Parameter(description = "ID del dentista", required = true)
        @PathVariable Long id,
        @Parameter(description = "Nuevos datos del dentista", required = true)
        @Valid @RequestBody DentistaRequest request) {
        DentistaResponse response = service.actualizarDentista(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dentista", description = "Elimina un dentista por su ID")
    @ApiResponse(responseCode = "204", description = "Dentista eliminado")
    public ResponseEntity<Void> eliminarDentista(
        @Parameter(description = "ID del dentista a eliminar", required = true)
        @PathVariable Long id) {
        service.eliminarDentista(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Paso 3: Documentar DTOs

```java
package com.clinica.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
    name = "DentistaRequest",
    description = "DTO para crear o actualizar un dentista"
)
public record DentistaRequest(
    @Schema(
        description = "Nombre del dentista",
        example = "Juan",
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100)
    String nombre,

    @Schema(
        description = "Apellido del dentista",
        example = "García López",
        minLength = 2,
        maxLength = 100
    )
    @NotBlank(message = "El apellido es requerido")
    @Size(min = 2, max = 100)
    String apellido,

    @Schema(
        description = "Teléfono de contacto",
        example = "+34 912 345 678",
        pattern = "^[0-9\\-\\+\\s\\(\\)]{7,20}$"
    )
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(regexp = "^[0-9\\-\\+\\s\\(\\)]{7,20}$")
    String telefono,

    @Schema(
        description = "Especialidad odontológica",
        example = "ORTODONCIA",
        allowableValues = {"ORTODONCIA", "PERIODONCIA", "ENDODONCIA", "IMPLANTOLOGIA"}
    )
    @NotBlank(message = "La especialidad es requerida")
    String especialidad
) {}

@Schema(
    name = "DentistaResponse",
    description = "DTO para retornar datos de un dentista"
)
public record DentistaResponse(
    @Schema(description = "ID único del dentista", example = "1")
    Long id,

    @Schema(description = "Nombre del dentista", example = "Juan")
    String nombre,

    @Schema(description = "Apellido del dentista", example = "García López")
    String apellido,

    @Schema(description = "Teléfono de contacto", example = "+34 912 345 678")
    String telefono,

    @Schema(description = "Especialidad", example = "ORTODONCIA")
    String especialidad,

    @Schema(description = "Pacientes asociados")
    List<PacienteResponse> pacientes
) {}
```

### Acceder a Swagger:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/v3/api-docs`
- YAML: `http://localhost:8080/v3/api-docs.yaml`

---

## 3. VALIDACIONES AVANZADAS

### Actualizar DTOs con validaciones mejoradas:

```java
package com.clinica.model.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

public record DentistaRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(example = "Juan", description = "Nombre del dentista")
    String nombre,

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(example = "García", description = "Apellido del dentista")
    String apellido,

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(
        regexp = "^[0-9\\-\\+\\s\\(\\)]{7,20}$",
        message = "El teléfono debe tener un formato válido (7-20 caracteres)"
    )
    @Schema(example = "+34 912 345 678", description = "Teléfono de contacto")
    String telefono,

    @NotBlank(message = "La especialidad es requerida")
    @Schema(
        example = "ORTODONCIA",
        description = "Especialidad del dentista",
        allowableValues = {"ORTODONCIA", "PERIODONCIA", "ENDODONCIA", "IMPLANTOLOGIA"}
    )
    String especialidad
) {}

public record PacienteRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100)
    @Schema(example = "María", description = "Nombre del paciente")
    String nombre,

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100)
    @Schema(example = "López", description = "Apellido del paciente")
    String apellido,

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    @Schema(example = "maria@example.com", description = "Email único del paciente")
    String email,

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(
        regexp = "^[0-9\\-\\+\\s\\(\\)]{7,20}$",
        message = "El teléfono tiene formato inválido"
    )
    @Schema(example = "+34 912 345 678", description = "Teléfono de contacto")
    String telefono,

    @NotNull(message = "El dentista es requerido")
    @Positive(message = "El ID del dentista debe ser un número positivo")
    @Schema(example = "1", description = "ID del dentista asignado")
    Long idDentista
) {}
```

---

## 4. SPRING DATA SPECIFICATIONS

### Paso 1: Crear clases de especificación

```java
// src/main/java/com/clinica/repository/spec/DentistaSpecification.java
package com.clinica.repository.spec;

import com.clinica.model.Dentista;
import com.clinica.model.enums.Especialidad;
import org.springframework.data.jpa.domain.Specification;

public class DentistaSpecification {

    public static Specification<Dentista> porNombre(String nombre) {
        return (root, query, cb) -> 
            nombre == null || nombre.isEmpty() ? null : 
            cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Dentista> porApellido(String apellido) {
        return (root, query, cb) -> 
            apellido == null || apellido.isEmpty() ? null : 
            cb.like(cb.lower(root.get("apellido")), "%" + apellido.toLowerCase() + "%");
    }

    public static Specification<Dentista> porEspecialidad(String especialidad) {
        return (root, query, cb) -> {
            if (especialidad == null || especialidad.isEmpty()) return null;
            try {
                return cb.equal(root.get("especialidad"), Especialidad.valueOf(especialidad));
            } catch (IllegalArgumentException e) {
                return null;
            }
        };
    }

    public static Specification<Dentista> porTelefono(String telefono) {
        return (root, query, cb) -> 
            telefono == null || telefono.isEmpty() ? null : 
            cb.like(root.get("telefono"), "%" + telefono + "%");
    }
}
```

### Paso 2: Actualizar Repositorio

```java
package com.clinica.repository;

import com.clinica.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long>, 
                                            JpaSpecificationExecutor<Dentista> {
}
```

### Paso 3: Actualizar Service

```java
@Service
public class DentistaService {
    // ...
    
    @Transactional(readOnly = true)
    public Page<DentistaResponse> buscarDentistas(
            String nombre,
            String apellido,
            String especialidad,
            String telefono,
            Pageable pageable) {
        
        log.info("Buscando dentistas con criterios: nombre={}, apellido={}, especialidad={}", 
                 nombre, apellido, especialidad);
        
        Specification<Dentista> spec = Specification.where(null)
            .and(DentistaSpecification.porNombre(nombre))
            .and(DentistaSpecification.porApellido(apellido))
            .and(DentistaSpecification.porEspecialidad(especialidad))
            .and(DentistaSpecification.porTelefono(telefono));
        
        Page<Dentista> page = repository.findAll(spec, pageable);
        return page.map(this::toResponse);
    }
}
```

### Paso 4: Actualizar Controller

```java
@RestController
@RequestMapping("/dentista")
@Tag(name = "Dentista Management")
public class DentistaController {
    
    @GetMapping("/buscar")
    @Operation(summary = "Buscar dentistas con filtros",
               description = "Permite búsqueda avanzada por múltiples criterios")
    public ResponseEntity<Page<DentistaResponse>> buscar(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String apellido,
        @RequestParam(required = false) String especialidad,
        @RequestParam(required = false) String telefono,
        Pageable pageable) {
        
        Page<DentistaResponse> page = service.buscarDentistas(
            nombre, apellido, especialidad, telefono, pageable);
        
        return ResponseEntity.ok(page);
    }
}
```

**Ejemplo de uso:**
```
GET /dentista/buscar?nombre=Juan&especialidad=ORTODONCIA&page=0&size=10&sort=nombre,asc
```

---

## 5. AUDITORÍA COMPLETA

### Paso 1: Crear clase base auditable

```java
// src/main/java/com/clinica/model/base/BaseAuditableEntity.java
package com.clinica.model.base;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseAuditableEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(length = 100)
    private String lastModifiedBy;
}
```

### Paso 2: Crear AuditorAware

```java
// src/main/java/com/clinica/config/AuditConfig.java
package com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                return Optional.of(auth.getName());
            }
            return Optional.of("SYSTEM");
        };
    }
}
```

### Paso 3: Actualizar entidades (Dentista)

```java
package com.clinica.model;

import com.clinica.model.base.BaseAuditableEntity;
import com.clinica.model.dto.DentistaRequest;
import com.clinica.model.enums.Especialidad;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dentista")
public class Dentista extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String telefono;

    @Enumerated(EnumType.STRING)
    private Especialidad especialidad;

    @OneToMany(mappedBy = "dentista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes;

    public Dentista(DentistaRequest request) {
        this.nombre = request.nombre();
        this.apellido = request.apellido();
        this.telefono = request.telefono();
        this.especialidad = Especialidad.valueOf(request.especialidad());
    }
}
```

### Paso 4: Lo mismo para Paciente

```java
package com.clinica.model;

import com.clinica.model.base.BaseAuditableEntity;
import com.clinica.model.dto.PacienteRequest;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paciente")
public class Paciente extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    public Paciente(PacienteRequest request, Dentista dentista) {
        this.nombre = request.nombre();
        this.apellido = request.apellido();
        this.email = request.email();
        this.telefono = request.telefono();
        this.dentista = dentista;
    }
}
```

---

## 6. RATE LIMITING

### Paso 1: Agregar dependencia

```xml
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.0.0</version>
</dependency>
```

### Paso 2: Crear anotación personalizada

```java
// src/main/java/com/clinica/annotation/RateLimit.java
package com.clinica.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    int requestsPerMinute() default 100;
    String name() default "";
}
```

### Paso 3: Crear interceptor

```java
// src/main/java/com/clinica/config/RateLimitInterceptor.java
package com.clinica.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String clientId = getClientId(request);
        Bucket bucket = buckets.computeIfAbsent(clientId, k -> createBucket());

        if (!bucket.tryConsume(1)) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setHeader("X-RateLimit-Remaining", "0");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Maximum 100 requests per minute.\"}");
            return false;
        }

        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.estimateAbilityToConsume(1).getRoundedTokensToConsume()));

        return true;
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    private String getClientId(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            clientIp = forwardedFor.split(",")[0].trim();
        }
        return clientIp;
    }
}
```

### Paso 4: Registrar interceptor

```java
// Actualizar SecurityConfig.java o crear WebConfig.java
package com.clinica.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/actuator/**");
    }
}
```

---

## 📊 VERIFICACIÓN DESPUÉS DE IMPLEMENTAR

### Compilación:
```powershell
cd C:\Workspace\Eclipse\clinica
mvn clean compile -DskipTests
```

### Ejecución de Tests:
```powershell
mvn test
```

### Ejecutar la aplicación:
```powershell
mvn spring-boot:run
```

### Verificar endpoints:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`

---

## 🎯 PRÓXIMOS PASOS RECOMENDADOS

1. **Implementar Logging primero** (máximo impacto, mínimo esfuerzo)
2. **Agregar Swagger** (documentación automática)
3. **Validaciones avanzadas** (mejor UX)
4. **Specifications** (búsqueda flexible)
5. **Auditoría** (compliance)
6. **Rate Limiting** (seguridad)

