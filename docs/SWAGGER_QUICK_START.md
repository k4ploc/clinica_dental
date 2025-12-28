# 🚀 Guía Rápida: Acceso a Swagger UI

## 1️⃣ Ejecutar la Aplicación

```bash
cd C:\Workspace\Eclipse\clinica
mvn spring-boot:run
```

O si tienes el JAR empaquetado:

```bash
java -jar target/clinica-0.0.1-SNAPSHOT.jar
```

## 2️⃣ Acceder a Swagger UI

Una vez que la aplicación esté en ejecución, abre tu navegador e ingresa:

```
http://localhost:8080/swagger-ui.html
```

## 3️⃣ Interfaz de Swagger UI

Verás una interfaz interactiva con dos secciones principales:

### 📌 Tag: **Dentistas**
- `GET /dentista` - Listar todos los dentistas
- `GET /dentista/{id}` - Obtener un dentista específico
- `POST /dentista` - Crear un nuevo dentista
- `PUT /dentista/{id}` - Actualizar un dentista
- `DELETE /dentista/{id}` - Eliminar un dentista

### 📌 Tag: **Pacientes**
- `GET /pacientes` - Listar todos los pacientes
- `GET /pacientes/{id}` - Obtener un paciente específico
- `POST /pacientes` - Crear un nuevo paciente
- `PUT /pacientes/{id}` - Actualizar un paciente
- `DELETE /pacientes/{id}` - Eliminar un paciente

## 4️⃣ Probar un Endpoint

1. Haz clic en el endpoint que deseas probar
2. Haz clic en el botón **"Try it out"**
3. Completa los parámetros requeridos
4. Haz clic en **"Execute"**
5. Observa la respuesta en la sección de abajo

## 5️⃣ Otros Recursos Disponibles

| Recurso | URL |
|---------|-----|
| **Especificación OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **Especificación OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` |
| **Health Check** | `http://localhost:8080/actuator/health` |

## 6️⃣ Importar en Postman

1. Abre **Postman**
2. Click en **File → Import**
3. Selecciona la pestaña **URL**
4. Pega: `http://localhost:8080/v3/api-docs`
5. Click en **Import**

¡Listo! Todos los endpoints se importarán automáticamente en Postman.

## 7️⃣ Ejemplo de Request (GET dentistas)

```http
GET http://localhost:8080/dentista?page=0&size=10&sort=id,desc
```

**Respuesta esperada (200 OK)**:
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "telefono": "123456789",
      "especialidad": "ODONTOLOGIA_GENERAL",
      "createdAt": "2025-12-21T10:00:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

## 8️⃣ Ejemplo de Request (POST paciente)

```http
POST http://localhost:8080/pacientes
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "García",
  "email": "carlos@example.com",
  "telefono": "987654321",
  "numeroSeguridadSocial": "SS123456"
}
```

**Respuesta esperada (201 Created)**:
```json
{
  "id": 1,
  "nombre": "Carlos",
  "apellido": "García",
  "email": "carlos@example.com",
  "telefono": "987654321",
  "numeroSeguridadSocial": "SS123456",
  "createdAt": "2025-12-21T10:00:00"
}
```

## ❓ Solución de Problemas

### ❌ No puedo acceder a Swagger UI
- **Verifica**: ¿La aplicación está ejecutándose?
- **Verifica**: ¿El puerto 8080 está disponible?
- **Verifica**: URL correcta: `http://localhost:8080/swagger-ui.html`

### ❌ Error 404 en Swagger UI
- Asegúrate de que `springdoc-openapi-starter-webmvc-ui` está en el `pom.xml`
- Ejecuta `mvn clean install` y reinicia la aplicación

### ❌ Controladores no aparecen en Swagger
- Asegúrate de que los controladores tienen `@RestController`
- Asegúrate de que tienen `@RequestMapping` o `@GetMapping`, etc.
- Reinicia la aplicación

---

## 📚 Documentación Completa

Para información detallada, revisa: `/docs/SWAGGER_OPENAPI.md`

---

**¡Disfruta explorando tu API!** 🎉
