# Fix: CORS y JWT - Endpoints Públicos

## Problema Identificado

1. **CORS bloqueado**: El frontend en `http://localhost:5173` no podía acceder a endpoints como `/dentista` y `/pacientes` debido a que CORS solo estaba configurado para `/api/**`.

2. **JWT requerido en endpoints públicos**: Los endpoints de `/api/auth/**` (login, register, validate) requerían token JWT incorrectamente.

## Soluciones Implementadas

### 1. SecurityConfig.java - Ampliar cobertura de CORS

**Cambio realizado**:
- Modificado `corsConfigurationSource()` para registrar CORS en `/**` en lugar de solo `/api/**`

```java
// Antes:
source.registerCorsConfiguration("/api/**", configuration);

// Después:
source.registerCorsConfiguration("/**", configuration);
```

**Beneficio**: Todos los endpoints ahora permiten CORS desde orígenes autorizados.

### 2. JwtAuthenticationFilter.java - Excluir rutas públicas

**Cambio realizado**:
- Agregado método `isPublicRoute()` que verifica si una ruta es pública
- Modificado `doFilterInternal()` para saltar validación JWT en rutas públicas

**Rutas públicas excluidas**:
- `/` (raíz)
- `/api/auth/**` (autenticación: login, register, validate)
- `/api/public/**` (endpoints públicos generales)
- `/actuator/health` (health check)
- `/swagger-ui/**` (documentación OpenAPI)
- `/v3/api-docs/**` (OpenAPI docs)

**Beneficio**: Los endpoints de autenticación ya no requieren token JWT.

## Configuración de Orígenes CORS

El archivo `application.properties` contiene:

```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200,http://localhost:5173}
```

**Orígenes permitidos por defecto**:
- `http://localhost:3000` (Angular)
- `http://localhost:4200` (Angular CLI)
- `http://localhost:5173` (Vite/Vue/React)

Para cambiar en producción, establecer variable de entorno `CORS_ALLOWED_ORIGINS`.

## Flujo de Autenticación

1. **Usuario se autentica**:
   ```
   POST /api/auth/login
   Content-Type: application/json
   
   {
     "username": "usuario",
     "password": "contraseña"
   }
   ```

2. **Servidor devuelve token** (sin requerir autenticación previa):
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

3. **Cliente usa token en requests autenticados**:
   ```
   GET /dentista?page=0&size=10
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

## Headers CORS Configurados

### Headers Permitidos:
- `Authorization`
- `Content-Type`
- `X-Requested-With`
- `Accept`
- `Origin`
- `Access-Control-Request-Method`
- `Access-Control-Request-Headers`

### Headers Expuestos:
- `Authorization`
- `Content-Disposition`

### Métodos HTTP Permitidos:
- GET, POST, PUT, DELETE, PATCH, OPTIONS

### Credenciales:
- ✅ Permitidas (para cookies y auth headers)

### Cache de Preflight:
- 1 hora (3600 segundos)

## Verificación

Para verificar que los cambios funcionan:

1. **Endpoint público (sin token)**:
   ```bash
   curl -H "Origin: http://localhost:5173" \
     http://localhost:8080/dentista?page=0&size=10
   ```
   ✅ Debe devolver `Access-Control-Allow-Origin: http://localhost:5173`

2. **Login (sin token requerido)**:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"admin"}'
   ```
   ✅ Debe devolver un token JWT

3. **Endpoint protegido (con token)**:
   ```bash
   curl -H "Authorization: Bearer <token>" \
     http://localhost:8080/pacientes?page=0&size=10
   ```
   ✅ Debe devolver datos del usuario autenticado

## Archivos Modificados

1. `src/main/java/com/clinica/config/SecurityConfig.java`
   - Cambio en `corsConfigurationSource()`

2. `src/main/java/com/clinica/config/JwtAuthenticationFilter.java`
   - Agregado método `isPublicRoute()`
   - Modificado método `doFilterInternal()`

## Notas de Seguridad

- ✅ CSRF deshabilitado (no aplica a APIs JWT stateless)
- ✅ Sesiones configuradas como stateless (SessionCreationPolicy.STATELESS)
- ✅ Solo endpoints configurados explícitamente permiten acceso sin autenticación
- ✅ Form login y HTTP Basic deshabilitados (usar JWT)
- ✅ Credenciales no se exponen en los headers de respuesta
