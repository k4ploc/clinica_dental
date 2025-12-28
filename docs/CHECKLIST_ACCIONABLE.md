# ✅ CHECKLIST ACCIONABLE - Optimizaciones Pendientes

**Generado:** 14 de Diciembre, 2025  
**Estado Actual:** 53% Completado (10/19)  
**Siguiente Paso:** Semana 1 - CRUD Completion

---

## 🔴 BLOQUEADORES - HACER HOY

### [ ] 1. Implementar GET /pacientes/{id}
**Archivo:** `PacienteController.java`
**Línea:** ~42
**Código actual:**
```java
@GetMapping("/{id}")
public Map<String, Object> obtenerPaciente(@PathVariable String id) {
    return null;
}
```
**Código necesario:**
```java
@GetMapping("/{id}")
public ResponseEntity<PacienteResponse> obtenerPaciente(@PathVariable Long id) {
    PacienteResponse response = pacienteService.obtenerPaciente(id);
    return ResponseEntity.ok(response);
}
```
**En Servicio:** Agregar método `obtenerPaciente(Long id)`
**Estimado:** 1.5 horas

---

### [ ] 2. Implementar PUT /pacientes/{id}
**Archivo:** `PacienteController.java`
**Código necesario:**
```java
@PutMapping("/{id}")
public ResponseEntity<PacienteResponse> actualizarPaciente(
        @PathVariable Long id,
        @Valid @RequestBody PacienteRequest request) {
    PacienteResponse response = pacienteService.actualizarPaciente(id, request);
    return ResponseEntity.ok(response);
}
```
**En Servicio:** Agregar método `actualizarPaciente(Long id, PacienteRequest request)`
**Estimado:** 1.5 horas

---

### [ ] 3. Implementar DELETE /pacientes/{id}
**Archivo:** `PacienteController.java`
**Código necesario:**
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
    pacienteService.eliminarPaciente(id);
    return ResponseEntity.noContent().build();
}
```
**En Servicio:** Agregar método `eliminarPaciente(Long id)`
**Estimado:** 1 hora

---

### [ ] 4. Activar Caché en Application
**Archivo:** `ClinicaApplication.java`
**Cambio necesario:**
```java
@SpringBootApplication
@EnableCaching  // ← AGREGAR ESTO
public class ClinicaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicaApplication.class, args);
    }
}
```
**Estimado:** 5 minutos

---

## 🟠 ALTA PRIORIDAD - ESTA SEMANA

### [ ] 5. Tests Unitarios para Servicios
**Archivos:** 
- `test/PacienteServiceTest.java`
- `test/DentistaServiceTest.java`

**Tests necesarios:**
```java
@SpringBootTest
class PacienteServiceTest {
    
    @Test
    void testObtenerPaciente() { }
    
    @Test
    void testActualizarPaciente() { }
    
    @Test
    void testEliminarPaciente() { }
    
    @Test
    void testCrearPaciente_DuplicateEmail() { }
}
```
**Estimado:** 3 horas

---

### [ ] 6. Tests de Controladores (MockMvc)
**Archivo:** `test/PacienteControllerTest.java`

**Tests necesarios:**
```java
@WebMvcTest(PacienteController.class)
class PacienteControllerTest {
    
    @Test
    void testGetPaciente_Success() { }
    
    @Test
    void testGetPaciente_NotFound() { }
    
    @Test
    void testPostPaciente_Success() { }
    
    @Test
    void testPostPaciente_ValidationError() { }
}
```
**Estimado:** 2 horas

---

### [ ] 7. Hacer build final con tests
**Comando:**
```bash
mvn clean test
mvn clean package
mvn verify
```
**Estimado:** 0.5 horas

---

## 🟡 IMPORTANTE - PRÓXIMA SEMANA

### [ ] 8. Implementar Specification para Filtros
**Archivo:** `PacienteRepository.java`
**Cambio:**
```java
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>,
                                            JpaSpecificationExecutor<Paciente> {
    boolean existsByEmail(String email);
}
```

**En Servicio:** Agregar método buscar con Specification
**Estimado:** 2 horas

---

### [ ] 9. Agregar Paginación
**Archivo:** `PacienteController.java`
**Código:**
```java
@GetMapping
public ResponseEntity<Page<PacienteResponse>> listPacientes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    Page<Paciente> pacientes = pacienteService.listarPaginado(
        PageRequest.of(page, size)
    );
    return ResponseEntity.ok(pacientes.map(this::toResponse));
}
```
**Estimado:** 1 hora

---

### [ ] 10. Mejorar Validaciones
**Archivos:** DTOs en `model/dto/`

**Agregar:**
```java
public record PacienteRequest(
    @NotBlank String nombre,
    @NotBlank String apellido,
    @NotBlank
    @Pattern(regexp = "^[0-9]{10}$")
    String telefono,
    @Email String email,
    @NotNull
    @Positive
    Long idDentista
) {}
```
**Estimado:** 1 hora

---

## 🟢 FUTURO - ROADMAP

### [ ] 11. Implementar JWT Authentication
**Dependencias a agregar:**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

**Archivos necesarios:**
- `JwtTokenProvider.java`
- `JwtAuthenticationFilter.java`
- `AuthController.java`

**Estimado:** 4 horas

---

### [ ] 12. Rate Limiting
**Dependencia:**
```xml
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>
```

**Archivo:** `RateLimitInterceptor.java`
**Estimado:** 2 horas

---

### [ ] 13. Swagger/OpenAPI
**Dependencia:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>
```

**Anotaciones en controladores:**
```java
@Api(tags = "Pacientes")
@PostMapping
@Operation(summary = "Crear paciente")
public ResponseEntity<PacienteResponse> crearPaciente(...) { }
```

**Estimado:** 1 hora

---

### [ ] 14. Soft Deletes
**Migración:** `V6__add_soft_delete.sql`
```sql
ALTER TABLE paciente ADD COLUMN deleted_at TIMESTAMP NULL;
```

**En Entidad:**
```java
@Where(clause = "deleted_at IS NULL")
public class Paciente { }
```

**Estimado:** 1 hora

---

## 📊 TIMELINE ESTIMADO

```
SEMANA 1 (Esta semana)
├─ Lunes: CRUD GET/PUT/DELETE       (4h)
├─ Martes: Tests unitarios           (5h)
├─ Miércoles: Tests controladores    (2h)
└─ Jueves: Build + Validación       (1h)
TOTAL: ~12 horas

SEMANA 2
├─ Specification filters    (2h)
├─ Paginación              (1h)
├─ Validaciones mejoradas  (1h)
└─ Tests adicionales       (2h)
TOTAL: ~6 horas

SEMANA 3
├─ JWT                     (4h)
├─ Rate limiting          (2h)
└─ Tests seguridad        (2h)
TOTAL: ~8 horas

SEMANA 4
├─ Swagger                 (1h)
├─ Soft deletes           (1h)
├─ Logs centralizados     (2h)
└─ Documentación          (2h)
TOTAL: ~6 horas

═════════════════════════════
GRAN TOTAL: ~32 horas (4 sprints)
```

---

## 📋 PRE-REQUISITOS ANTES DE EMPEZAR

### Verificaciones:
- [ ] Git cliente instalado
- [ ] Maven 3.9.9+
- [ ] JDK 21 configurado
- [ ] PostgreSQL corriendo
- [ ] Rama feature creada

### Configuración:
```bash
# Crear rama para CRUD
git checkout -b feature/crud-completion

# Verificar compilación
mvn clean compile

# Ejecutar tests actuales
mvn test
```

---

## 🎯 DEFINICIÓN DE HECHO

Cada feature está completa cuando:

### ✅ Código
- [ ] Método implementado
- [ ] Sigue arquitectura (controller → service → repo)
- [ ] Usa DTOs con Records
- [ ] Validación con @Valid

### ✅ Tests
- [ ] Test unitario para servicio
- [ ] Test de controlador (MockMvc)
- [ ] Casos de error incluidos
- [ ] 80%+ cobertura

### ✅ Documentación
- [ ] JavaDoc en métodos públicos
- [ ] Comentarios en lógica compleja
- [ ] Ejemplo de uso en test

### ✅ Calidad
- [ ] Compila sin warnings
- [ ] Todos los tests pasan
- [ ] SonarQube OK (si disponible)

---

## 🚀 COMANDOS ÚTILES

```bash
# Compilar
mvn clean compile

# Tests
mvn test
mvn test -Dtest=PacienteControllerTest

# Build
mvn clean package -DskipTests

# Ejecutar aplicación
mvn spring-boot:run

# Check style
mvn checkstyle:check

# Generar report
mvn site:site
```

---

## 📞 SOPORTE

**Si encuentras problemas:**

1. **Compilación:** Verifica Java 21
2. **Tests:** Verifica BD test
3. **Dependencias:** `mvn dependency:tree`
4. **Errores:** Revisa los reportes en `OPTIMIZATIONS_APPLIED.md`

---

## ✅ COMPLETAR CHECKLIST

Marca cada item conforme lo completes:

```
SEMANA 1:
☐ GET /pacientes/{id}
☐ PUT /pacientes/{id}
☐ DELETE /pacientes/{id}
☐ @EnableCaching en Application
☐ Tests unitarios
☐ Tests controladores
☐ Build final

SEMANA 2:
☐ Specification filters
☐ Paginación
☐ Validación mejorada
☐ Tests adicionales

SEMANA 3:
☐ JWT
☐ Rate limiting
☐ Tests seguridad

SEMANA 4:
☐ Swagger
☐ Soft deletes
☐ Logs
☐ Documentación
```

---

**Checklist creado:** 14/12/2025  
**Estado:** Listo para implementar  
**Próximo paso:** Comenzar con CRUD esta semana

