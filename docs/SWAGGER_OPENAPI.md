# Implementación de Swagger/OpenAPI - Clínica API

## 📋 Descripción General

Se ha implementado **SpringDoc OpenAPI** para proporcionar documentación automática e interactiva de la API REST de la Clínica. Esto permite a los desarrolladores y consumidores de la API explorar, entender y probar los endpoints de forma intuitiva.

---

## 🚀 Características Implementadas

### 1. **Dependencia SpringDoc OpenAPI**
- Librería: `springdoc-openapi-starter-webmvc-ui`
- Versión: `2.8.4`
- Compatible con Spring Boot 3.5.5 y Java 21

### 2. **Interfaz Swagger UI**
- URL: `http://localhost:8080/swagger-ui.html`
- Interfaz interactiva para explorar y probar endpoints
- Esquemas visuales de request/response

### 3. **Documentación OpenAPI en JSON**
- URL: `http://localhost:8080/v3/api-docs`
- Formato: JSON estructurado según especificación OpenAPI 3.0

### 4. **Documentación OpenAPI en YAML**
- URL: `http://localhost:8080/v3/api-docs.yaml`
- Formato: YAML para facilitar integración con otras herramientas

---

## 📝 Configuración Implementada

### Archivo: `OpenApiConfig.java`

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clínica API")
                        .description("API REST para la gestión de dentistas y pacientes")
                        .version("1.0.0")
                        .contact(...)
                        .license(...))
                .addServersItem(...);
    }
}
```

**Detalles:**
- **Título**: "Clínica API"
- **Descripción**: Documentación clara del propósito de la API
- **Versión**: 1.0.0
- **Contacto**: Información de soporte
- **Licencia**: MIT License
- **Servidores**: Configurado para desarrollo y producción

### Propiedades en `application.properties`

```properties
# --- SpringDoc OpenAPI Configuration (Swagger) ---
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.use-root-path=true
```

**Configuraciones:**
- `api-docs.path`: Ruta para acceder a la especificación OpenAPI
- `swagger-ui.path`: Ruta para la interfaz Swagger UI
- `swagger-ui.enabled`: Habilita la interfaz visual
- `operations-sorter`: Ordena operaciones por método HTTP
- `tags-sorter`: Ordena tags alfabéticamente
- `use-root-path`: Utiliza la ruta raíz para la UI

---

## 📚 Anotaciones en Controladores

### DentistaController

Todas las operaciones han sido documentadas con:

```java
@Tag(name = "Dentistas", description = "Operaciones relacionadas con dentistas")
public class DentistaController {
    
    @GetMapping
    @Operation(summary = "Listar dentistas", description = "Obtiene una lista paginada...")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de dentistas obtenida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de paginación inválidos")
    })
    public ResponseEntity<Page<DentistaResponse>> getDentistas(
            @Parameter(description = "Información de paginación")
            Pageable pageable) { ... }
    
    // ... más operaciones
}
```

### PacienteController

Configurado de forma similar con documentación completa para:
- Listar pacientes
- Crear paciente
- Obtener paciente por ID
- Actualizar paciente
- Eliminar paciente

---

## 🔍 Anotaciones Utilizadas

| Anotación | Propósito |
|-----------|----------|
| `@Tag` | Agrupa operaciones relacionadas |
| `@Operation` | Describe un endpoint específico |
| `@ApiResponse` | Documenta respuestas posibles (códigos HTTP) |
| `@ApiResponses` | Colección de `@ApiResponse` |
| `@Parameter` | Describe parámetros de entrada |
| `@RequestBody` | Documenta el cuerpo de la solicitud |
| `@Schema` | Define estructura de datos |

---

## 🧪 Códigos de Respuesta Documentados

### Dentistas
- **200**: Operación exitosa
- **201**: Recurso creado exitosamente
- **204**: Recurso eliminado exitosamente
- **400**: Datos inválidos
- **404**: Recurso no encontrado

### Pacientes
- **200**: Operación exitosa
- **201**: Recurso creado exitosamente
- **204**: Recurso eliminado exitosamente
- **400**: Datos inválidos
- **404**: Recurso no encontrado

---

## 🚀 Cómo Usar

### 1. **Iniciar la aplicación**

```bash
mvn spring-boot:run
```

O ejecutar el JAR empaquetado:

```bash
java -jar target/clinica-0.0.1-SNAPSHOT.jar
```

### 2. **Acceder a Swagger UI**

Abre el navegador y ve a: `http://localhost:8080/swagger-ui.html`

### 3. **Explorar los endpoints**

- Verás todos los endpoints organizados por tags (Dentistas, Pacientes)
- Cada endpoint muestra:
  - Descripción
  - Parámetros requeridos
  - Estructura de request/response
  - Códigos de respuesta posibles

### 4. **Probar endpoints interactivamente**

- Haz clic en "Try it out"
- Completa los parámetros
- Haz clic en "Execute"
- Observa la respuesta

### 5. **Descargar especificación OpenAPI**

En la interfaz Swagger UI, puedes descargar:
- `swagger-ui-bundle.js`
- Especificación en JSON o YAML

---

## 📦 Integración con Herramientas Externas

### Postman
1. Abre Postman
2. File → Import → URL
3. Pega: `http://localhost:8080/v3/api-docs`
4. Postman importará automáticamente todos los endpoints

### Cliente OpenAPI Generator
```bash
openapi-generator-cli generate -i http://localhost:8080/v3/api-docs -g <language> -o <output>
```

---

## 📊 Métodos HTTP Documentados

### GET
- `GET /dentista` - Listar dentistas paginados
- `GET /dentista/{id}` - Obtener dentista específico
- `GET /pacientes` - Listar pacientes paginados
- `GET /pacientes/{id}` - Obtener paciente específico

### POST
- `POST /dentista` - Crear nuevo dentista
- `POST /pacientes` - Crear nuevo paciente

### PUT
- `PUT /dentista/{id}` - Actualizar dentista
- `PUT /pacientes/{id}` - Actualizar paciente

### DELETE
- `DELETE /dentista/{id}` - Eliminar dentista
- `DELETE /pacientes/{id}` - Eliminar paciente

---

## 🔒 Consideraciones de Seguridad

Swagger UI está habilitada por defecto para facilitar el desarrollo. Para producción, considera:

1. **Deshabilitar en producción** (si es necesario):
```properties
springdoc.swagger-ui.enabled=${SWAGGER_UI_ENABLED:false}
```

2. **Proteger con Spring Security** (implementar si se requiere autenticación)

3. **Usar JWT o OAuth2** para endpoints que requieran autenticación

---

## 🛠️ Archivos Modificados

| Archivo | Cambios |
|---------|---------|
| `pom.xml` | Agregada dependencia `springdoc-openapi-starter-webmvc-ui` |
| `application.properties` | Configuración de SpringDoc OpenAPI |
| `DentistaController.java` | Anotaciones OpenAPI en todos los métodos |
| `PacienteController.java` | Anotaciones OpenAPI en todos los métodos |
| `OpenApiConfig.java` | Nuevo archivo - Configuración de OpenAPI |

---

## 📖 Referencias Oficiales

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)
- [Spring Boot Starter Web](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html)

---

## ✅ Verificación

Para verificar que Swagger está funcionando correctamente:

```bash
# 1. Compilar el proyecto
mvn clean compile

# 2. Ejecutar tests (si los hay)
mvn test

# 3. Empaquetar
mvn clean package

# 4. Ejecutar la aplicación
mvn spring-boot:run

# 5. Acceder a http://localhost:8080/swagger-ui.html
```

---

## 📝 Notas Adicionales

- La configuración es **totalmente documentada** usando anotaciones
- **Sin configuración manual compleja** gracias a SpringDoc
- **Compatible con herramientas estándar** (Postman, Insomnia, etc.)
- **Documentación siempre sincronizada** con el código
- **Interfaz moderna y responsive** en Swagger UI 4.x

---

**Última actualización**: Diciembre 21, 2025
