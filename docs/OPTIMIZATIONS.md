# ⚡ Guía de Optimizaciones

## 📋 Tabla de Contenidos
1. [Problemas Identificados](#problemas-identificados)
2. [Optimizaciones de Base de Datos](#optimizaciones-de-base-de-datos)
3. [Optimizaciones de Código](#optimizaciones-de-código)
4. [Optimizaciones de Seguridad](#optimizaciones-de-seguridad)
5. [Optimizaciones de Performance](#optimizaciones-de-performance)
6. [Optimizaciones de Docker](#optimizaciones-de-docker)

---

## 🔴 Problemas Identificados

### 1. **Version de Java Incompatible** ⚠️ CRÍTICO

**Problema:**
```
Docker: maven:3.9.9-eclipse-temurin-25 no existe
Maven: Java 25 no es LTS (Long Term Support)
```

**Impacto:** 
- ❌ No compila en Docker
- ⚠️ Versión no estable

**Solución Recomendada:**
```xml
<!-- En pom.xml -->
<properties>
    <maven.compiler.release>21</maven.compiler.release>
    <java.version>21</java.version>
</properties>
```

```dockerfile
# En Dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
```

**Beneficios:**
- ✅ Compila correctamente en Docker
- ✅ Java 21 es LTS (soporte hasta 2031)
- ✅ Mayor estabilidad

---

### 2. **Seguridad Desactivada** ⚠️ CRÍTICO

**Problema:**
```java
// SecurityConfig.java
.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
.csrf(AbstractHttpConfigurer::disable)
```

**Impacto:**
- ❌ Acceso libre sin autenticación
- ❌ Vulnerable a CSRF
- ⚠️ No hay autorización por roles

**Solución:** Ver sección [Optimizaciones de Seguridad](#optimizaciones-de-seguridad)

---

### 3. **Ciclos en Respuestas API** ⚠️ MODERADO

**Problema:**
```json
GET /dentista
{
  "id": 1,
  "pacientes": [
    {
      "dentista": { ... }  // Ciclo potencial
    }
  ]
}
```

**Impacto:**
- ⚠️ Serialización circular
- ❌ Respuestas ineficientes

**Solución:** Ya implementada usando DTOs sin referencias circulares

---

### 4. **Métodos Incompletos** ⚠️ MODERADO

**Problema:**
```java
// PacienteController.java
@GetMapping("/{id}")
public Map<String, Object> obtenerPaciente(@PathVariable String id) {
    return null;  // No implementado
}
```

**Impacto:**
- ❌ Funcionalidad incompleta
- ⚠️ ID retorna null

---

### 5. **Falta de Índices en Base de Datos** ⚠️ BAJO

**Problema:**
```sql
-- Sem índices en campos consultados frecuentemente
SELECT * FROM paciente WHERE email = 'test@example.com';  -- Sin índice
SELECT * FROM paciente WHERE dentista_id = 1;             -- Sin índice
```

**Impacto:**
- ⚠️ Queries lentas con muchos datos
- 📉 Performance degradado

---

## 🗄️ Optimizaciones de Base de Datos

### 1. Agregar Índices

```sql
-- Crear índices en campos clave
CREATE INDEX idx_paciente_email ON paciente(email);
CREATE INDEX idx_paciente_dentista_id ON paciente(dentista_id);
CREATE INDEX idx_cita_paciente_id ON cita(paciente_id);
CREATE INDEX idx_cita_dentista_id ON cita(dentista_id);
CREATE INDEX idx_cita_fecha ON cita(fecha);
```

**Migración Flyway:**
```sql
-- V4__add_indexes.sql
CREATE INDEX idx_paciente_email ON paciente(email);
CREATE INDEX idx_paciente_dentista_id ON paciente(dentista_id);
CREATE INDEX idx_cita_paciente_id ON cita(paciente_id);
CREATE INDEX idx_cita_dentista_id ON cita(dentista_id);
CREATE INDEX idx_cita_fecha ON cita(fecha);
```

**Impacto:**
- ✅ Queries ~10x más rápidas
- ✅ Mejor performance con grandes volúmenes

---

### 2. Agregar Timestamps

```sql
-- V5__add_timestamps.sql
ALTER TABLE dentista 
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE paciente
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE cita
    ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
```

**Modelo Java:**
```java
@Entity
public class Paciente {
    // ...
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

**Beneficios:**
- ✅ Auditoría de cambios
- ✅ Filtrado por fecha
- ✅ Debugging más fácil

---

### 3. Implementar Soft Delete

```sql
-- V6__add_soft_delete.sql
ALTER TABLE dentista ADD COLUMN deleted_at TIMESTAMP NULL;
ALTER TABLE paciente ADD COLUMN deleted_at TIMESTAMP NULL;
ALTER TABLE cita ADD COLUMN deleted_at TIMESTAMP NULL;
```

**Modelo Java:**
```java
@Entity
@Where(clause = "deleted_at IS NULL")
public class Paciente {
    // ...
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;
}
```

**Beneficios:**
- ✅ Recuperación de datos eliminados
- ✅ Auditoría completa
- ✅ Cumplimiento de regulaciones

---

### 4. Optimizar Relaciones

**Antes - N+1 Problem:**
```java
List<Dentista> dentistas = repository.findAll();
for (Dentista d : dentistas) {
    d.getPacientes();  // N queries adicionales!
}
```

**Después - Eager Loading:**
```java
@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {
    @Query("SELECT d FROM Dentista d LEFT JOIN FETCH d.pacientes")
    List<Dentista> findAllWithPacientes();
}
```

---

## 💻 Optimizaciones de Código

### 1. Usar Records para DTOs

**Antes:**
```java
public class DentistaResponse {
    private Long id;
    private String nombre;
    // ... getters, setters, constructor
}
```

**Después:**
```java
public record DentistaResponse(
    Long id,
    String nombre,
    String apellido,
    String telefono,
    String especialidad,
    List<PacienteResponse> pacientes
) {}
```

**Beneficios:**
- ✅ Menos boilerplate
- ✅ Immutable (más seguro)
- ✅ Performance mejorado

---

### 2. Usar Specification para Filtros

**Antes:**
```java
// 10 métodos findBy...
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findByNombre(String nombre);
    List<Paciente> findByApellido(String apellido);
    List<Paciente> findByEmail(String email);
    List<Paciente> findByDentistaId(Long dentistaId);
    List<Paciente> findByNombreAndApellido(String nombre, String apellido);
    // ...
}
```

**Después:**
```java
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>, 
                                            JpaSpecificationExecutor<Paciente> {
    boolean existsByEmail(String email);
}

// En servicio
public class PacienteService {
    public List<PacienteResponse> buscar(String nombre, Long dentistaId) {
        Specification<Paciente> spec = Specification.where(null);
        
        if (nombre != null) {
            spec = spec.and((root, query, cb) -> 
                cb.like(root.get("nombre"), "%" + nombre + "%")
            );
        }
        
        if (dentistaId != null) {
            spec = spec.and((root, query, cb) -> 
                cb.equal(root.get("dentista").get("id"), dentistaId)
            );
        }
        
        return repository.findAll(spec).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }
}
```

**Beneficios:**
- ✅ Filtrado dinámico
- ✅ Reutilizable
- ✅ Menos métodos en repositorio

---

### 3. Caché con @Cacheable

```java
@Service
@EnableCaching
public class DentistaService {
    
    @Cacheable(value = "dentistas")
    public List<DentistaResponse> getDentistas() {
        List<Dentista> lista = repository.findAll();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }
    
    @CacheEvict(value = "dentistas", allEntries = true)
    public Dentista createDentista(DentistaRequest request) {
        return repository.save(new Dentista(request));
    }
}
```

**Beneficios:**
- ✅ ~100x más rápido en segunda llamada
- ✅ Menos queries a BD
- ✅ Mejora UX

**Configuración (pom.xml):**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

---

### 4. Validación Mejorada

```java
@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    
    @PostMapping
    public ResponseEntity<PacienteResponse> crearPaciente(
            @Valid @RequestBody PacienteRequest request) {
        PacienteResponse response = pacienteService.crearPaciente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

// DTO mejorado
public record PacienteRequest(
    @NotBlank(message = "Nombre requerido") 
    String nombre,
    
    @NotBlank(message = "Apellido requerido") 
    String apellido,
    
    @NotBlank(message = "Teléfono requerido")
    @Pattern(regexp = "^[0-9]{10}$", message = "Teléfono debe tener 10 dígitos")
    String telefono,
    
    @Email(message = "Email inválido")
    @NotBlank(message = "Email requerido")
    String email,
    
    @NotNull(message = "ID dentista requerido")
    @Positive(message = "ID dentista debe ser positivo")
    @JsonProperty("id_dentista") 
    Long idDentista
) {}
```

---

## 🔐 Optimizaciones de Seguridad

### 1. Implementar JWT Authentication

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
```

---

### 2. Implementar Roles y Permisos

```java
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
}

public enum Role {
    ADMIN, DENTISTA, PACIENTE
}

// SecurityConfig
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/dentista/**").hasRole("DENTISTA")
            .anyRequest().authenticated()
        )
        .addFilter(jwtAuthenticationFilter());
        return http.build();
    }
}
```

---

### 3. HTTPS/TLS

```yaml
# application.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
server.ssl.enabled=true
```

---

### 4. Rate Limiting

```xml
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    private final Bucket bucket = Bucket4j.builder()
        .addLimit(Limit.of(100, Refill.intervally(100, Duration.ofMinutes(1))))
        .build();
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        if (!bucket.tryConsume(1)) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            return false;
        }
        return true;
    }
}
```

---

## 📊 Optimizaciones de Performance

### 1. Paginación

```java
// Controlador
@GetMapping
public ResponseEntity<Page<PacienteResponse>> listPacientes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Page<Paciente> pacientes = pacienteService.listarPaginado(
        PageRequest.of(page, size)
    );
    return ResponseEntity.ok(
        pacientes.map(this::toResponse)
    );
}

// Servicio
public Page<Paciente> listarPaginado(Pageable pageable) {
    return repository.findAll(pageable);
}
```

**Query resultante:**
```sql
SELECT * FROM paciente LIMIT 10 OFFSET 0;
```

---

### 2. Proyecciones DTO

```java
// Interface de proyección
public interface PacienteDto {
    Long getId();
    String getNombre();
    String getApellido();
}

// Repository
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<PacienteDto> findAllProjectedBy();
}

// Solo trae los campos necesarios
```

---

### 3. Connection Pooling

```yaml
# application.properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

---

## 🐳 Optimizaciones de Docker

### 1. Reducir tamaño de imagen

**Antes: ~900MB**
```dockerfile
FROM maven:3.9.9-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN mvn package
CMD ["java", "-jar", "target/clinica-0.0.1-SNAPSHOT.jar"]
```

**Después: ~350MB**
```dockerfile
# Stage 1: Builder
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests

# Stage 2: Runtime (minimal)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Beneficios:**
- ✅ 60% más pequeño
- ✅ Descarga más rápida
- ✅ Menos vulnerabilidades

---

### 2. JVM Optimization

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar /app/app.jar
```

---

### 3. Health Checks

```yaml
# docker-compose.yml
services:
  app:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
```

---

## 📋 Checklist de Optimizaciones

### Críticas (Implementar YA)
- [ ] Cambiar Java 25 → 21
- [ ] Implementar autenticación
- [ ] Activar CSRF protection
- [ ] Agregar índices a BD

### Altas (Próximas 2 semanas)
- [ ] Completar CRUD endpoints
- [ ] Agregar tests unitarios
- [ ] Implementar caché
- [ ] Mejorar validaciones

### Medias (Próximas 4 semanas)
- [ ] Agregar paginación
- [ ] Implementar JWT
- [ ] Rate limiting
- [ ] Logs centralizados

### Bajas (Backlog)
- [ ] Swagger/OpenAPI
- [ ] Soft deletes
- [ ] GraphQL API
- [ ] Websockets

---

**Última actualización:** Diciembre 2025

