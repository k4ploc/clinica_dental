# URLs de Swagger - Acceso Público

## 🎉 Swagger ahora está disponible SIN autenticación

La configuración de seguridad ha sido actualizada para permitir acceso público a todas las URLs de Swagger y OpenAPI.

---

## URLs Disponibles

### 1. **Swagger UI (Interfaz Interactiva)**
```
http://localhost:8080/swagger-ui.html
```
- Interfaz gráfica interactiva para probar los endpoints
- Permite ver todos los endpoints disponibles
- Permite ejecutar requests directamente desde el navegador

### 2. **OpenAPI JSON**
```
http://localhost:8080/v3/api-docs
```
- Especificación completa de la API en formato JSON
- Útil para integración con herramientas automatizadas

### 3. **OpenAPI YAML**
```
http://localhost:8080/v3/api-docs.yaml
```
- Especificación completa de la API en formato YAML
- Formato legible para documentación

### 4. **Swagger UI Alternativo**
```
http://localhost:8080/swagger-ui/
```
- Directorio raíz de los recursos de Swagger UI

---

## Cambios Realizados en SecurityConfig

Se agregaron las siguientes rutas al permitlist en `SecurityConfig.java`:

```java
.requestMatchers(
    "/swagger-ui.html",
    "/swagger-ui/**",
    "/v3/api-docs",
    "/v3/api-docs/**",
    "/v3/api-docs.yaml"
).permitAll()
```

---

## Endpoints Públicos Disponibles

Los siguientes endpoints NO requieren autenticación:

| Endpoint | Descripción |
|----------|-------------|
| `/` | Página de inicio |
| `/api/public/**` | APIs públicas |
| `/actuator/health` | Health check |
| `/swagger-ui.html` | Swagger UI |
| `/swagger-ui/**` | Recursos de Swagger UI |
| `/v3/api-docs` | OpenAPI JSON |
| `/v3/api-docs/**` | Documentación OpenAPI |
| `/v3/api-docs.yaml` | OpenAPI YAML |

---

## Cómo Probar

### Opción 1: Desde el Navegador (Recomendado)
1. Abre: `http://localhost:8080/swagger-ui.html`
2. Verás una interfaz interactiva con todos los endpoints
3. Haz clic en "Try it out" para probar cualquier endpoint

### Opción 2: Con cURL
```bash
# Ver la documentación JSON
curl http://localhost:8080/v3/api-docs

# Ver la documentación YAML
curl http://localhost:8080/v3/api-docs.yaml
```

### Opción 3: Con Postman
1. Importa la especificación OpenAPI
2. URL: `http://localhost:8080/v3/api-docs`
3. Todos los endpoints estarán disponibles en Postman

---

## Información de la API

- **Título:** Clínica API
- **Versión:** 1.0.0
- **Descripción:** API REST para la gestión de dentistas y pacientes en la clínica
- **Servidores Configurados:**
  - `http://localhost:8080` (Desarrollo)
  - `https://api.clinica.com` (Producción)

---

## Ejemplo de Endpoints Disponibles

Una vez en Swagger UI, deberías ver endpoints como:

- `GET /pacientes` - Obtener lista de pacientes
- `POST /pacientes` - Crear nuevo paciente
- `GET /dentistas` - Obtener lista de dentistas
- `POST /dentistas` - Crear nuevo dentista
- Y más...

---

## Notas de Seguridad

⚠️ **IMPORTANTE:**
- Las URLs de Swagger están públicas SOLO para DESARROLLO y TESTING
- En producción, se debe restringir el acceso a Swagger usando:
  - Autenticación básica
  - JWT tokens
  - Firewall/IP whitelist
  - Variable de entorno para deshabilitar Swagger en prod

---

## Próximos Pasos

Una vez verificues que Swagger funciona correctamente:

1. ✅ Probar los endpoints desde Swagger UI
2. ✅ Verificar que la documentación es correcta
3. ✅ Configurar autenticación JWT (si es necesario)
4. ✅ Implementar protección de Swagger en producción

---

## Contenedor Docker

El contenedor está ejecutándose con:
- **Nombre:** `clinica-swagger`
- **Puerto:** 8080
- **Imagen:** `clinica:latest`

Para ver los logs:
```powershell
docker logs clinica-swagger -f
```

Para detener el contenedor:
```powershell
docker stop clinica-swagger
```
