# 🔐 AUTENTICACIÓN JWT - GUÍA COMPLETA

## ✅ Sistema de Autenticación Implementado

Se ha implementado un completo sistema de autenticación JWT que permite:
- ✅ Generar tokens JWT para usuarios autenticados
- ✅ Validar tokens en cada petición a la API
- ✅ Acceso público a Swagger sin autenticación
- ✅ Protección de endpoints de la API

---

## 📋 Usuarios de Demostración

Para desarrollo y testing, use estos usuarios:

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| `admin` | `admin123` | ADMIN |
| `dentista` | `dentista123` | DENTISTA |
| `paciente` | `paciente123` | PACIENTE |

---

## 🚀 Cómo Obtener un Token JWT

### Opción 1: Con cURL

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d {
    \"username\": \"admin\",
    \"password\": \"admin123\"
  }
```

### Opción 2: Con Swagger UI

1. Abre: http://localhost:8080/swagger-ui.html
2. Busca el endpoint: **POST /api/auth/login**
3. Haz clic en "Try it out"
4. Rellena los campos:
   - username: `admin`
   - password: `admin123`
5. Haz clic en "Execute"
6. Copia el `access_token` de la respuesta

### Opción 3: Con Postman

1. Método: **POST**
2. URL: `http://localhost:8080/api/auth/login`
3. Body (JSON):
```json
{
  "username": "admin",
  "password": "admin123"
}
```
4. Click "Send"
5. Copia el `access_token`

---

## 📝 Respuesta del Endpoint de Login

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.abc123..."
}
```

---

## 🔒 Cómo Usar el Token en Peticiones

### Opción 1: Header Authorization

```bash
curl -X GET http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.abc123..."
```

### Opción 2: Con Swagger UI

1. Abre: http://localhost:8080/swagger-ui.html
2. Haz clic en el botón **"Authorize"** (arriba a la derecha)
3. Pega el token en el campo:
   ```
   Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.abc123...
   ```
4. Haz clic en "Authorize"
5. Ahora todos los endpoints estarán autorizados

### Opción 3: Con Postman

1. En la pestaña **"Authorization"** de tu request
2. Type: **Bearer Token**
3. Token: `eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.abc123...`
4. Send

---

## 🌐 Endpoints de Autenticación

### 1. Login (Generar Token)
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Respuesta:
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Validar Token
```
POST /api/auth/validate?token=eyJhbGciOiJIUzI1NiJ9...

Respuesta exitosa (200):
Token válido para usuario: admin

Respuesta fallida (401):
Token inválido o expirado
```

---

## 📚 Endpoints Públicos (SIN Token)

```
GET     /                          - Inicio
GET     /api/public/**             - APIs públicas
GET     /actuator/health           - Health check
POST    /api/auth/login            - Generar token ✅ NUEVO
POST    /api/auth/validate         - Validar token ✅ NUEVO
GET     /swagger-ui.html           - Swagger UI
GET     /swagger-ui/**             - Recursos Swagger
GET     /v3/api-docs               - OpenAPI JSON
GET     /v3/api-docs/**            - OpenAPI docs
```

---

## 🔐 Endpoints Protegidos (CON Token)

```
GET     /api/pacientes/*           - Requiere autenticación ✅
POST    /api/pacientes/*           - Requiere autenticación ✅
GET     /api/dentistas/*           - Requiere autenticación ✅
POST    /api/dentistas/*           - Requiere autenticación ✅
```

---

## ⏱️ Validez del Token

- **Duración:** 24 horas (86,400 segundos)
- **Después de expirar:** Debes hacer login nuevamente
- **Header requerido:** `Authorization: Bearer {token}`

---

## 🧪 Ejemplo Completo: Paso a Paso

### Paso 1: Generar Token
```bash
# Request
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Response
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.ABC123..."
}
```

### Paso 2: Usar Token en Petición Protegida
```bash
# Request con token
curl -X GET http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.ABC123..."

# Response
[
  {...paciente data...},
  {...paciente data...}
]
```

---

## ❌ Errores Comunes

### Error: "Token inválido"
```json
{
  "status": 401,
  "message": "Acceso denegado"
}
```
**Solución:** Asegúrate de incluir el header `Authorization: Bearer {token}`

### Error: "Token expirado"
```json
{
  "status": 401,
  "message": "Token expirado"
}
```
**Solución:** Haz login nuevamente para obtener un nuevo token

### Error: "Credenciales inválidas"
```json
{
  "access_token": "Credenciales inválidas"
}
```
**Solución:** Verifica que el username y password sean correctos

---

## 🔧 Configuración JWT

**Archivo:** `application.properties`

```properties
app.jwt.secret=MyVerySecureSecretKeyForJWTTokenGenerationAndValidation12345
app.jwt.expiration=86400000  # 24 horas en milisegundos
```

### Para Cambiar la Duración

1. Abre: `src/main/resources/application.properties`
2. Cambia: `app.jwt.expiration=86400000`
   - 3600000 = 1 hora
   - 86400000 = 24 horas (default)
   - 604800000 = 7 días

---

## 🚀 Flujo de Autenticación Completo

```
┌─────────────────────────────────────────────────────────┐
│                   CLIENTE                               │
└────────────────┬────────────────────────────────────────┘
                 │
                 │ 1. POST /api/auth/login
                 │    username, password
                 ↓
┌─────────────────────────────────────────────────────────┐
│              SERVIDOR (SecurityConfig)                  │
│  - Autentica credenciales                              │
│  - Verifica username/password                           │
└────────────────┬────────────────────────────────────────┘
                 │
                 │ 2. Genera JWT Token (JwtService)
                 ↓
┌─────────────────────────────────────────────────────────┐
│           RESPUESTA: {access_token: "..."}             │
└────────────────┬────────────────────────────────────────┘
                 │
                 │ 3. Guarda token en cliente
                 │
                 │ 4. GET /api/pacientes
                 │    Header: Authorization: Bearer {token}
                 ↓
┌─────────────────────────────────────────────────────────┐
│         FILTRO JWT (JwtAuthenticationFilter)            │
│  - Extrae token del header                              │
│  - Valida token (JwtService)                            │
│  - Establece autenticación en SecurityContext           │
└────────────────┬────────────────────────────────────────┘
                 │
                 │ 5. Acceso permitido ✅
                 ↓
┌─────────────────────────────────────────────────────────┐
│           RESPUESTA: [pacientes...]                     │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Verificación Rápida

```bash
# 1. Verificar que el servidor está activo
curl http://localhost:8080/actuator/health

# 2. Verificar que Swagger está accesible
# Abre: http://localhost:8080/swagger-ui.html

# 3. Obtener token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 4. Usar token
curl -X GET http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer {token_aqui}"
```

---

**Documentación:** JWT Authentication System  
**Fecha:** 21 de Diciembre de 2025  
**Status:** ✅ Completamente Funcional
