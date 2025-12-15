# 🔌 Documentación de API REST

## 📋 Tabla de Contenidos
1. [Introducción](#introducción)
2. [Autenticación](#autenticación)
3. [Códigos de Estado](#códigos-de-estado)
4. [Dentistas Endpoints](#dentistas-endpoints)
5. [Pacientes Endpoints](#pacientes-endpoints)
6. [Manejo de Errores](#manejo-de-errores)
7. [Ejemplos de Uso](#ejemplos-de-uso)

---

## 📖 Introducción

### URL Base
```
Local:   http://localhost:8080
Docker:  http://localhost:9090
```

### Características
- 🔄 Arquitectura REST
- 📝 JSON para request/response
- ⚡ Sin autenticación requerida (actualmente)
- 🔒 CSRF desactivado (⚠️ requiere seguridad en producción)

### Headers Comunes
```
Content-Type: application/json
Accept: application/json
```

---

## 🔐 Autenticación

**Estado Actual:** ❌ No implementada

La seguridad está desactivada permitiendo acceso libre a todos los endpoints.

**Para producción, implementar:**
- JWT (JSON Web Tokens)
- OAuth 2.0
- Bearer Tokens

---

## 📊 Códigos de Estado

| Código | Significado | Caso de Uso |
|--------|------------|-----------|
| **200** | OK | Operación exitosa |
| **201** | Created | Recurso creado exitosamente |
| **204** | No Content | Recurso eliminado |
| **400** | Bad Request | Datos inválidos |
| **401** | Unauthorized | Credenciales requeridas |
| **409** | Conflict | Recurso duplicado (email existe) |
| **500** | Internal Server Error | Error del servidor |

---

## 🦷 Dentistas Endpoints

### 1. Obtener Todos los Dentistas

**Endpoint:**
```http
GET /dentista
```

**Descripción:** Retorna lista de todos los dentistas con sus pacientes asociados.

**Parámetros:**
- Ninguno

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "García",
    "telefono": "3001234567",
    "especialidad": "ORTODONCISTA",
    "pacientes": [
      {
        "id": 1,
        "nombre": "Carlos",
        "apellido": "López",
        "telefono": "3109876543",
        "email": "carlos@example.com"
      },
      {
        "id": 2,
        "nombre": "María",
        "apellido": "Rodríguez",
        "telefono": "3112345678",
        "email": "maria@example.com"
      }
    ]
  },
  {
    "id": 2,
    "nombre": "Maria",
    "apellido": "López",
    "telefono": "3012345678",
    "especialidad": "DENTISTA",
    "pacientes": []
  }
]
```

**cURL:**
```bash
curl -X GET "http://localhost:9090/dentista" \
  -H "Content-Type: application/json"
```

**JavaScript/Fetch:**
```javascript
fetch('http://localhost:9090/dentista')
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

**Python/Requests:**
```python
import requests

response = requests.get('http://localhost:9090/dentista')
dentistas = response.json()
print(dentistas)
```

---

### 2. Crear Dentista

**Endpoint:**
```http
POST /dentista
Content-Type: application/json
```

**Descripción:** Crea un nuevo dentista en el sistema.

**Request Body:**
```json
{
  "nombre": "Juan",
  "apellido": "García",
  "telefono": "3001234567",
  "especialidad": "ORTODONCISTA"
}
```

**Validaciones:**
- `nombre`: Requerido (string)
- `apellido`: Requerido (string)
- `telefono`: Requerido (string)
- `especialidad`: Requerido, debe ser uno de: `DENTISTA`, `CIRUJANO`, `ORTODONCISTA`

**Response (200 OK):**
```json
{
  "id": 3,
  "nombre": "Juan",
  "apellido": "García",
  "telefono": "3001234567",
  "especialidad": "ORTODONCISTA",
  "pacientes": null
}
```

**Response (400 Bad Request):**
```json
{
  "especialidad": "Especialidad es obligatoria"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:9090/dentista" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "García",
    "telefono": "3001234567",
    "especialidad": "ORTODONCISTA"
  }'
```

**JavaScript/Fetch:**
```javascript
const dentista = {
  nombre: "Juan",
  apellido: "García",
  telefono: "3001234567",
  especialidad: "ORTODONCISTA"
};

fetch('http://localhost:9090/dentista', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(dentista)
})
.then(response => response.json())
.then(data => console.log('Dentista creado:', data))
.catch(error => console.error('Error:', error));
```

**Python/Requests:**
```python
import requests

dentista = {
    "nombre": "Juan",
    "apellido": "García",
    "telefono": "3001234567",
    "especialidad": "ORTODONCISTA"
}

response = requests.post(
    'http://localhost:9090/dentista',
    json=dentista
)
print(response.json())
```

---

## 👥 Pacientes Endpoints

### 1. Obtener Todos los Pacientes

**Endpoint:**
```http
GET /pacientes
```

**Descripción:** Retorna lista de todos los pacientes.

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Carlos",
    "apellido": "López",
    "telefono": "3109876543",
    "email": "carlos@example.com"
  },
  {
    "id": 2,
    "nombre": "María",
    "apellido": "Rodríguez",
    "telefono": "3112345678",
    "email": "maria@example.com"
  }
]
```

**cURL:**
```bash
curl -X GET "http://localhost:9090/pacientes" \
  -H "Content-Type: application/json"
```

---

### 2. Crear Paciente

**Endpoint:**
```http
POST /pacientes
Content-Type: application/json
```

**Descripción:** Crea un nuevo paciente asociado a un dentista.

**Request Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com",
  "id_dentista": 1
}
```

**Validaciones:**
- `nombre`: Requerido (string)
- `apellido`: Requerido (string)
- `telefono`: Requerido (string)
- `email`: Requerido, debe ser único
- `id_dentista`: Requerido (long), debe existir en base de datos

**Response (200 OK):**
```
✅ Paciente creado con ID: 
```

**Response (400 Bad Request):**
```json
{
  "nombre": "Nombre requerido",
  "email": "Email inválido"
}
```

**Response (409 Conflict):**
```json
{
  "error": "El email ya se registro"
}
```

**cURL:**
```bash
curl -X POST "http://localhost:9090/pacientes" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellido": "López",
    "telefono": "3109876543",
    "email": "carlos@example.com",
    "id_dentista": 1
  }'
```

**JavaScript/Fetch:**
```javascript
const paciente = {
  nombre: "Carlos",
  apellido: "López",
  telefono: "3109876543",
  email: "carlos@example.com",
  id_dentista: 1
};

fetch('http://localhost:9090/pacientes', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(paciente)
})
.then(response => response.text())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

---

### 3. Obtener Paciente por ID

**Endpoint:**
```http
GET /pacientes/{id}
```

**Descripción:** Obtiene un paciente específico por su ID.

**Parámetros:**
- `id` (path parameter): ID del paciente

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Carlos",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com"
}
```

**Response (404 Not Found):**
```json
{
  "error": "Paciente no encontrado"
}
```

**Estado:** ⚠️ Implementación pendiente (retorna null actualmente)

**cURL:**
```bash
curl -X GET "http://localhost:9090/pacientes/1" \
  -H "Content-Type: application/json"
```

---

### 4. Eliminar Paciente

**Endpoint:**
```http
DELETE /pacientes/{id}
```

**Descripción:** Elimina un paciente del sistema.

**Parámetros:**
- `id` (path parameter): ID del paciente

**Response (200 OK):**
```
🗑️ Paciente eliminado con ID: 1
```

**cURL:**
```bash
curl -X DELETE "http://localhost:9090/pacientes/1" \
  -H "Content-Type: application/json"
```

**JavaScript/Fetch:**
```javascript
fetch('http://localhost:9090/pacientes/1', {
  method: 'DELETE',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.text())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

---

## ⚠️ Manejo de Errores

### Errores de Validación

**Caso:** Crear paciente sin nombre

**Request:**
```json
{
  "nombre": "",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com",
  "id_dentista": 1
}
```

**Response (400 Bad Request):**
```json
{
  "nombre": "Nombre requerido"
}
```

### Errores de Integridad

**Caso:** Email duplicado

**Request:**
```json
{
  "nombre": "Carlos",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com",  // Email ya existe
  "id_dentista": 1
}
```

**Response (409 Conflict):**
```json
{
  "error": "El email ya se registro"
}
```

### Errores de Referencia

**Caso:** Dentista no existe

**Request:**
```json
{
  "nombre": "Carlos",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com",
  "id_dentista": 999  // No existe
}
```

**Response (500 Internal Server Error):**
```json
{
  "error": "Dentista no encontrado"
}
```

---

## 📝 Ejemplos de Uso

### Ejemplo 1: Flujo Completo

**1. Crear un dentista:**
```bash
curl -X POST "http://localhost:9090/dentista" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "García",
    "telefono": "3001234567",
    "especialidad": "ORTODONCISTA"
  }'
```

**Respuesta:** Dentista creado con ID 1

**2. Crear pacientes para ese dentista:**
```bash
curl -X POST "http://localhost:9090/pacientes" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos",
    "apellido": "López",
    "telefono": "3109876543",
    "email": "carlos@example.com",
    "id_dentista": 1
  }'
```

**3. Obtener todos los dentistas con pacientes:**
```bash
curl -X GET "http://localhost:9090/dentista"
```

**Respuesta:** Lista con el dentista y sus pacientes asociados

### Ejemplo 2: Script Python

```python
import requests
import json

BASE_URL = "http://localhost:9090"

# 1. Crear dentista
dentista_data = {
    "nombre": "Juan",
    "apellido": "García",
    "telefono": "3001234567",
    "especialidad": "ORTODONCISTA"
}
dentista_response = requests.post(f"{BASE_URL}/dentista", json=dentista_data)
dentista = dentista_response.json()
dentista_id = dentista['id']
print(f"✅ Dentista creado: {dentista}")

# 2. Crear paciente
paciente_data = {
    "nombre": "Carlos",
    "apellido": "López",
    "telefono": "3109876543",
    "email": "carlos@example.com",
    "id_dentista": dentista_id
}
paciente_response = requests.post(f"{BASE_URL}/pacientes", json=paciente_data)
print(f"✅ Paciente creado: {paciente_response.text}")

# 3. Obtener todos los dentistas
dentistas = requests.get(f"{BASE_URL}/dentista").json()
print(f"✅ Dentistas en el sistema:")
for d in dentistas:
    print(f"  - {d['nombre']} {d['apellido']} ({d['especialidad']})")
    if d['pacientes']:
        for p in d['pacientes']:
            print(f"    • {p['nombre']} {p['apellido']} ({p['email']})")

# 4. Obtener todos los pacientes
pacientes = requests.get(f"{BASE_URL}/pacientes").json()
print(f"✅ Pacientes en el sistema:")
for p in pacientes:
    print(f"  - {p['nombre']} {p['apellido']} ({p['email']})")
```

### Ejemplo 3: JavaScript (Node.js)

```javascript
const BASE_URL = 'http://localhost:9090';

async function main() {
  try {
    // 1. Crear dentista
    const dentistaRes = await fetch(`${BASE_URL}/dentista`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nombre: 'Juan',
        apellido: 'García',
        telefono: '3001234567',
        especialidad: 'ORTODONCISTA'
      })
    });
    const dentista = await dentistaRes.json();
    console.log('✅ Dentista creado:', dentista);

    // 2. Crear paciente
    const pacienteRes = await fetch(`${BASE_URL}/pacientes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        nombre: 'Carlos',
        apellido: 'López',
        telefono: '3109876543',
        email: 'carlos@example.com',
        id_dentista: dentista.id
      })
    });
    const pacienteMsg = await pacienteRes.text();
    console.log('✅ Paciente creado:', pacienteMsg);

    // 3. Obtener dentistas
    const dentistasRes = await fetch(`${BASE_URL}/dentista`);
    const dentistas = await dentistasRes.json();
    console.log('✅ Dentistas:', dentistas);

  } catch (error) {
    console.error('❌ Error:', error);
  }
}

main();
```

---

## 📚 Especificaciones Técnicas

### Especialidades Válidas
- `DENTISTA`
- `CIRUJANO`
- `ORTODONCISTA`

### Formatos de Datos
- **Teléfono:** String (sin formato específico)
- **Email:** String (validación de email)
- **ID:** Long (número entero positivo)

### Límites
- **Nombre/Apellido:** Máximo 100 caracteres
- **Email:** Máximo 150 caracteres, único
- **Teléfono:** Máximo 30 caracteres

---

## 🔄 Próximas Implementaciones

- [ ] Endpoint GET `/pacientes/{id}`
- [ ] Endpoint PUT/PATCH `/pacientes/{id}`
- [ ] Endpoint PUT/PATCH `/dentista/{id}`
- [ ] Filtros y búsqueda
- [ ] Paginación
- [ ] Autenticación JWT
- [ ] Documentación Swagger/OpenAPI

---

**Última actualización:** Diciembre 2025

