# CORS - Configuración y Troubleshooting

## Descripción General

CORS (Cross-Origin Resource Sharing) está configurado en **3 niveles** para máxima compatibilidad:

1. **WebConfig (nivel más general)** - Configuración a nivel de Spring Web
2. **SecurityConfig (nivel de seguridad)** - Configuración en el SecurityFilterChain
3. **@CrossOrigin (nivel de controlador)** - Anotación explícita en cada controlador

## Configuración Actual

### 1. WebConfig.java

Implementa `WebMvcConfigurer` para CORS global:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

**Beneficios:**
- Aplica a todas las rutas (`/**`)
- Se ejecuta antes de los filtros de seguridad
- Más confiable para solicitudes OPTIONS (preflight)

### 2. SecurityConfig.java

Configuración en el `SecurityFilterChain`:

```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

Con `CorsConfigurationSource`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

**Beneficios:**
- Integrado con Spring Security
- Respeta la configuración de autorización
- Aplica a solicitudes autenticadas y públicas

### 3. Anotaciones @CrossOrigin en Controladores

```java
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController { ... }

@RestController
@RequestMapping("/api/dentista")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class DentistaController { ... }

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PacienteController { ... }
```

**Beneficios:**
- Explícito y fácil de mantener
- Documentación clara en el código
- Soporte adicional para navegadores estrictos

## Orígenes Permitidos

### Configuración en application.properties

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200,http://localhost:5173}
```

### Orígenes por Defecto

- `http://localhost:3000` (Angular CLI, etc.)
- `http://localhost:4200` (Angular default)
- `http://localhost:5173` (Vite, Vue, React)

### Para Cambiar en Producción

Establecer variable de entorno:

```bash
# Linux/Mac
export CORS_ALLOWED_ORIGINS="https://miapp.com,https://admin.miapp.com"

# Windows PowerShell
$env:CORS_ALLOWED_ORIGINS = "https://miapp.com,https://admin.miapp.com"

# Windows CMD
set CORS_ALLOWED_ORIGINS=https://miapp.com,https://admin.miapp.com

# Docker
docker run -e CORS_ALLOWED_ORIGINS="https://miapp.com" ...
```

## Headers CORS Configurados

### Headers Permitidos en Solicitudes

```
Authorization
Content-Type
X-Requested-With
Accept
Origin
Access-Control-Request-Method
Access-Control-Request-Headers
```

### Headers Expuestos en Respuestas

```
Authorization
Content-Disposition
```

### Métodos HTTP Permitidos

```
GET
POST
PUT
DELETE
PATCH
OPTIONS
```

## Flujo de Solicitud Preflight (OPTIONS)

Cuando el navegador detecta una solicitud CORS "compleja", envía una solicitud **OPTIONS** (preflight):

```
OPTIONS /api/auth/login HTTP/1.1
Origin: http://localhost:5173
Access-Control-Request-Method: POST
Access-Control-Request-Headers: content-type
```

El servidor responde con:

```
HTTP/1.1 200 OK
Access-Control-Allow-Origin: http://localhost:5173
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, PATCH, OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

Luego el navegador envía la solicitud real (POST, PUT, etc.)

## Troubleshooting

### Error: "No 'Access-Control-Allow-Origin' header is present"

**Causas comunes:**

1. **El servidor no está respondiendo**: Verifica que `http://localhost:8080` esté activo
2. **Origen no permitido**: El origen no está en `CORS_ALLOWED_ORIGINS`
3. **Variable de entorno no establecida**: `CORS_ALLOWED_ORIGINS` no se leyó correctamente
4. **Caché del navegador**: Limpia la caché o abre en incógnito

**Solución:**

```bash
# 1. Verificar que el servidor está activo
curl -i http://localhost:8080/api/auth/login

# 2. Verificar headers CORS
curl -i -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST"

# 3. Revisar logs del servidor
tail -f logs/clinica.log | grep -i cors
```

### Error: "Credentials mode is 'include' but Access-Control-Allow-Credentials is missing"

**Causa**: Credenciales están habilitadas en el navegador pero no en el servidor

**Solución**: Ya está configurado en `application.properties`:

```properties
# SecurityConfig ya tiene:
configuration.setAllowCredentials(true);

# WebConfig también:
.allowCredentials(true)
```

### Error: "Method not allowed"

**Causa**: El método HTTP (GET, POST, etc.) no está permitido

**Solución**: Verificar que el método está en `allowedMethods`:

```java
.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
```

## Testing desde Frontend

### Con fetch API

```javascript
// Login
const response = await fetch('http://localhost:8080/api/auth/login', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    credentials: 'include', // Permitir cookies
    body: JSON.stringify({
        username: 'admin',
        password: 'admin'
    })
});

const { token } = await response.json();

// Usar token en solicitudes posteriores
const dentistasResponse = await fetch('http://localhost:8080/api/dentista?page=0&size=10', {
    method: 'GET',
    headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    },
    credentials: 'include'
});
```

### Con axios

```javascript
// Configurar instancia axios
const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true // CORS con credenciales
});

// Login
const { data: { token } } = await api.post('/api/auth/login', {
    username: 'admin',
    password: 'admin'
});

// Usar token
api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

const dentistas = await api.get('/api/dentista?page=0&size=10');
```

### Con cURL

```bash
# Solicitud OPTIONS (preflight)
curl -i -X OPTIONS http://localhost:8080/api/auth/login \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: content-type"

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5173" \
  -d '{"username":"admin","password":"admin"}'

# Con token
TOKEN="eyJhbGciOiJIUzI1NiIs..."
curl -X GET "http://localhost:8080/api/dentista?page=0&size=10" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Origin: http://localhost:5173"
```

## Archivos Relacionados

- `src/main/java/com/clinica/config/WebConfig.java` - Configuración WebMvc
- `src/main/java/com/clinica/config/SecurityConfig.java` - Configuración Security
- `src/main/java/com/clinica/controller/AuthController.java` - Endpoint de autenticación
- `src/main/java/com/clinica/controller/DentistaController.java` - Endpoint de dentistas
- `src/main/java/com/clinica/controller/PacienteController.java` - Endpoint de pacientes
- `src/main/resources/application.properties` - Propiedades de aplicación

## Referencias Oficiales

- [Spring CORS Documentation](https://spring.io/guides/gs/rest-service-cors/)
- [Spring Security CORS](https://docs.spring.io/spring-security/reference/features/exploiting/csrf/cors.html)
- [MDN - CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
