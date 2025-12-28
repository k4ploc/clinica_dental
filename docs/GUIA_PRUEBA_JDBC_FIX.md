# Guía de Prueba - Fix JDBC Connection Error

## Objetivo
Verificar que el error `InternalAuthenticationServiceException: Unable to commit against JDBC Connection` ha sido resuelto durante el login.

---

## Prerequisitos

1. **Base de datos PostgreSQL activa** con el usuario admin creado
2. **Variables de entorno configuradas**:
   ```powershell
   $env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/clinica"
   $env:SPRING_DATASOURCE_USERNAME = "postgres"
   $env:SPRING_DATASOURCE_PASSWORD = "tu_contraseña"
   $env:JWT_SECRET = "tu_secret_key"
   ```

3. **Proyecto compilado** (último BUILD SUCCESS verificado)

---

## Pasos de Prueba

### 1. Iniciar la Aplicación

```powershell
cd C:\Workspace\Eclipse\clinica
mvnw.cmd spring-boot:run
```

**Esperado**: La aplicación inicia en `http://localhost:8080`

### 2. Verificar Logs Iniciales

Busca en los logs:
```
Clinica Application Started Successfully
Database migrations completed
```

### 3. Probar Login via cURL

**Windows PowerShell**:
```powershell
$body = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $body
```

**Windows CMD**:
```cmd
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

### 4. Verificar Respuesta

**Respuesta Exitosa (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsI..."
}
```

**Error Anterior (CORREGIDO)**:
```
❌ InternalAuthenticationServiceException: Unable to commit against JDBC Connection
```

---

## Validación de Logs

### Logs Esperados Después del Fix

```
2025-12-24 22:33:53.675 DEBUG c.c.service.CustomUserDetailsService - Buscando usuario: admin
2025-12-24 22:33:53.676 DEBUG org.hibernate.SQL - select u1_0.id,... from usuario u1_0 left join fetch usuario_rol...
2025-12-24 22:33:53.681 DEBUG c.c.service.CustomUserDetailsService - Usuario encontrado: admin con 1 roles
2025-12-24 22:33:53.682 INFO  o.s.s.authentication.ProviderManager - Authentication successful for user 'admin'
```

### ✅ Indicadores de Éxito

- ✅ No hay `InternalAuthenticationServiceException`
- ✅ No hay `LazyInitializationException`
- ✅ Se recibe token JWT válido
- ✅ Log muestra: "Usuario encontrado"

### ❌ Si Aún hay Error

Si ves errores de JDBC:
1. Verifica que la BD está activa
2. Verifica credenciales en variables de entorno
3. Revisa que Flyway ejecutó las migraciones exitosamente
4. Busca logs de: `spring.jpa.open-in-view`

---

## Prueba 2: Usar el Token

```powershell
$token = "eyJhbGciOiJIUzI1NiJ9..." # Copiar del paso anterior

Invoke-WebRequest -Uri "http://localhost:8080/api/usuarios" `
  -Method GET `
  -Headers @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
  }
```

**Esperado**: Devuelve lista de usuarios con código 200

---

## Prueba 3: Login Incorrecto

```powershell
$body = @{
    username = "admin"
    password = "wrongpassword"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body $body
```

**Esperado**: Devuelve 401 Unauthorized con mensaje "Credenciales inválidas"

---

## Prueba 4: Swagger UI

Accede a: `http://localhost:8080/swagger-ui.html`

1. Expande "Autenticación" → POST /api/auth/login
2. Click en "Try it out"
3. Ingresa credenciales
4. Ejecuta y verifica respuesta

---

## Checklist Final

- [ ] Aplicación inicia sin errores
- [ ] Base de datos conecta correctamente
- [ ] Login con admin devuelve token
- [ ] No aparecen excepciones JDBC en logs
- [ ] Token puede usarse en endpoints protegidos
- [ ] Login con credenciales incorrectas devuelve 401

---

## Debugging Avanzado

Si persiste el error, habilita más logs:

**En `application.properties`**:
```properties
logging.level.org.springframework.security=TRACE
logging.level.org.hibernate.engine.transaction.internal=TRACE
logging.level.org.springframework.boot.autoconfigure.jdbc=TRACE
```

Luego reinicia la aplicación.

---

## Cambios Aplicados

| Archivo | Cambio | Razón |
|---------|--------|-------|
| Usuario.java | `EAGER` → `LAZY` | Evitar inicialización fuera de transacción |
| CustomUserDetailsService.java | Agregar `usuario.getRoles().size()` | Forzar inicialización dentro de transacción |

---

**Resultado Esperado**: ✅ Login sin errores JDBC  
**Fecha de Verificación**: 2025-12-24  
**Status**: Listo para probar
