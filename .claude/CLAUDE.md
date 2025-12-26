# Clínica Dental - Backend API

## Descripción
API REST para sistema de gestión de clínica dental. Backend desarrollado con Spring Boot 3.5.5, Java 21, PostgreSQL.

## URL Base
- **Desarrollo**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

## Autenticación JWT

El sistema usa JWT (JSON Web Token) para autenticación.

### Endpoints de Auth

#### POST /api/auth/register
Registra un nuevo usuario.

**Request:**
```json
{
  "username": "string (3-50 chars, requerido)",
  "password": "string (min 6 chars, requerido)",
  "email": "string (opcional)",
  "nombre": "string (opcional)",
  "apellido": "string (opcional)",
  "rol": "string (opcional, default: ROLE_PACIENTE)"
}
```

**Roles disponibles:**
- `ROLE_ADMIN` - Administrador con acceso completo
- `ROLE_DENTISTA` - Acceso a gestión de pacientes y citas
- `ROLE_RECEPCIONISTA` - Acceso a citas y pacientes
- `ROLE_PACIENTE` - Acceso limitado a su información

**Response (201):**
```
Usuario creado exitosamente con rol: ROLE_ADMIN
```

#### POST /api/auth/login
Autentica usuario y devuelve token JWT.

**Request:**
```json
{
  "username": "string (requerido)",
  "password": "string (requerido)"
}
```

**Response (200):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (401):**
```json
{
  "access_token": "Credenciales inválidas"
}
```

#### POST /api/auth/validate
Valida un token JWT.

**Query Param:** `token=<jwt_token>`

**Response (200):** `Token válido para usuario: admin`

### Uso del Token
Incluir en header de todas las peticiones protegidas:
```
Authorization: Bearer <access_token>
```

---

## Endpoints Protegidos

### Dentistas

**Base URL:** `/api/dentista`

#### GET /api/dentista
Lista dentistas paginados.

**Query Params:**
| Param | Default | Descripción |
|-------|---------|-------------|
| page | 0 | Número de página (0-indexed) |
| size | 10 | Elementos por página |
| sort | id | Campo de ordenamiento |
| direction | asc | Dirección (asc/desc) |

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez",
      "telefono": "123456789",
      "especialidad": "ORTODONCIA",
      "pacientes": []
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

#### GET /api/dentista/{id}
Obtiene un dentista por ID.

**Response (200):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "123456789",
  "especialidad": "ORTODONCIA",
  "pacientes": [...]
}
```

#### POST /api/dentista
Crea un nuevo dentista.

**Request:**
```json
{
  "nombre": "string",
  "apellido": "string",
  "telefono": "string",
  "especialidad": "string (requerido)"
}
```

**Especialidades válidas:** `ORTODONCIA`, `ENDODONCIA`, `PERIODONCIA`, `CIRUGIA`, `ODONTOPEDIATRIA`, `GENERAL`

**Response (201):** DentistaResponse

#### PUT /api/dentista/{id}
Actualiza un dentista.

**Request:** Mismo formato que POST

**Response (200):** DentistaResponse

#### DELETE /api/dentista/{id}
Elimina un dentista.

**Response (204):** No content

---

### Pacientes

**Base URL:** `/api/pacientes`

#### GET /api/pacientes
Lista pacientes paginados.

**Query Params:** Igual que dentistas (page, size, sort, direction)

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "nombre": "María",
      "apellido": "García",
      "telefono": "987654321",
      "email": "maria@email.com"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

#### GET /api/pacientes/{id}
Obtiene un paciente por ID.

**Response (200):** PacienteResponse

#### POST /api/pacientes
Crea un nuevo paciente.

**Request:**
```json
{
  "nombre": "string (requerido)",
  "apellido": "string (requerido)",
  "telefono": "string (requerido)",
  "email": "string (opcional)",
  "id_dentista": "number (requerido)"
}
```

**Response (201):** PacienteResponse

#### PUT /api/pacientes/{id}
Actualiza un paciente.

**Request:** Mismo formato que POST

**Response (200):** PacienteResponse

#### DELETE /api/pacientes/{id}
Elimina un paciente.

**Response (204):** No content

---

### Citas

**Base URL:** `/api/citas`

#### GET /api/citas
Lista citas paginadas.

**Query Params:**
| Param | Default | Descripción |
|-------|---------|-------------|
| page | 0 | Número de página (0-indexed) |
| size | 10 | Elementos por página |
| sort | fecha | Campo de ordenamiento |
| direction | asc | Dirección (asc/desc) |

**Response (200):**
```json
{
  "content": [
    {
      "id": 1,
      "fecha": "2025-01-15T10:00:00",
      "motivo": "Revision general",
      "paciente": {
        "id": 1,
        "nombre": "Maria",
        "apellido": "Garcia",
        "telefono": "987654321"
      },
      "dentista": {
        "id": 1,
        "nombre": "Juan",
        "apellido": "Perez",
        "especialidad": "ORTODONCIA"
      },
      "createdAt": "2025-01-10T08:30:00",
      "updatedAt": "2025-01-10T08:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```

#### GET /api/citas/{id}
Obtiene una cita por ID.

**Response (200):** CitaResponse

#### POST /api/citas
Crea una nueva cita.

**Request:**
```json
{
  "fecha": "2025-01-15T10:00:00 (requerido, debe ser futura)",
  "motivo": "string (opcional)",
  "id_paciente": "number (requerido)",
  "id_dentista": "number (requerido)"
}
```

**Response (201):** CitaResponse

**Errores:**
- `400` - El dentista ya tiene una cita programada para esa fecha y hora
- `404` - Paciente o dentista no encontrado

#### PUT /api/citas/{id}
Actualiza una cita.

**Request:** Mismo formato que POST

**Response (200):** CitaResponse

#### DELETE /api/citas/{id}
Elimina una cita.

**Response (204):** No content

#### GET /api/citas/paciente/{pacienteId}
Obtiene todas las citas de un paciente.

**Response (200):** `CitaResponse[]`

#### GET /api/citas/dentista/{dentistaId}
Obtiene todas las citas de un dentista.

**Response (200):** `CitaResponse[]`

#### GET /api/citas/rango
Obtiene citas en un rango de fechas.

**Query Params:**
| Param | Formato | Descripción |
|-------|---------|-------------|
| inicio | yyyy-MM-dd'T'HH:mm:ss | Fecha inicio (requerido) |
| fin | yyyy-MM-dd'T'HH:mm:ss | Fecha fin (requerido) |

**Ejemplo:** `/api/citas/rango?inicio=2025-01-01T00:00:00&fin=2025-12-31T23:59:59`

**Response (200):** `CitaResponse[]`

#### GET /api/citas/paciente/{pacienteId}/futuras
Obtiene las próximas citas de un paciente (solo futuras).

**Response (200):** `CitaResponse[]`

---

## Endpoints Públicos (sin auth)
- `GET /` - Root
- `GET /api/public/**` - Endpoints públicos
- `GET /actuator/health` - Health check
- `GET /swagger-ui.html` - Documentación Swagger
- `GET /v3/api-docs` - OpenAPI JSON
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro

---

## CORS
Orígenes permitidos (configurables en .env):
- `http://localhost:3000` (React dev server)
- `http://localhost:4200` (Angular dev server)

Métodos permitidos: `GET, POST, PUT, DELETE, PATCH, OPTIONS`

---

## Tipos TypeScript para React

```typescript
// Auth
interface AuthRequest {
  username: string;
  password: string;
}

interface AuthResponse {
  access_token: string;
}

interface RegisterRequest {
  username: string;
  password: string;
  email?: string;
  nombre?: string;
  apellido?: string;
  rol?: 'ROLE_ADMIN' | 'ROLE_DENTISTA' | 'ROLE_RECEPCIONISTA' | 'ROLE_PACIENTE';
}

// Dentista
interface DentistaRequest {
  nombre?: string;
  apellido?: string;
  telefono?: string;
  especialidad: string;
}

interface DentistaResponse {
  id: number;
  nombre: string;
  apellido: string;
  telefono: string;
  especialidad: string;
  pacientes: PacienteResponse[];
}

// Paciente
interface PacienteRequest {
  nombre: string;
  apellido: string;
  telefono: string;
  email?: string;
  id_dentista: number;
}

interface PacienteResponse {
  id: number;
  nombre: string;
  apellido: string;
  telefono: string;
  email: string;
}

// Cita
interface CitaRequest {
  fecha: string; // ISO 8601: "2025-01-15T10:00:00"
  motivo?: string;
  id_paciente: number;
  id_dentista: number;
}

interface CitaResponse {
  id: number;
  fecha: string;
  motivo: string;
  paciente: {
    id: number;
    nombre: string;
    apellido: string;
    telefono: string;
  };
  dentista: {
    id: number;
    nombre: string;
    apellido: string;
    especialidad: string;
  };
  createdAt: string;
  updatedAt: string;
}

// Paginación
interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}
```

---

## Ejemplo de Servicio API en React

```typescript
const API_URL = 'http://localhost:8080';

const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

export const api = {
  // Auth
  login: async (credentials: AuthRequest): Promise<AuthResponse> => {
    const res = await fetch(`${API_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });
    if (!res.ok) throw new Error('Login failed');
    return res.json();
  },

  // Dentistas
  getDentistas: async (page = 0, size = 10): Promise<Page<DentistaResponse>> => {
    const res = await fetch(
      `${API_URL}/api/dentista?page=${page}&size=${size}`,
      { headers: getAuthHeader() }
    );
    if (!res.ok) throw new Error('Failed to fetch dentistas');
    return res.json();
  },

  createDentista: async (data: DentistaRequest): Promise<DentistaResponse> => {
    const res = await fetch(`${API_URL}/api/dentista`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create dentista');
    return res.json();
  },

  // Pacientes
  getPacientes: async (page = 0, size = 10): Promise<Page<PacienteResponse>> => {
    const res = await fetch(
      `${API_URL}/api/pacientes?page=${page}&size=${size}`,
      { headers: getAuthHeader() }
    );
    if (!res.ok) throw new Error('Failed to fetch pacientes');
    return res.json();
  },

  createPaciente: async (data: PacienteRequest): Promise<PacienteResponse> => {
    const res = await fetch(`${API_URL}/api/pacientes`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create paciente');
    return res.json();
  },

  // Citas
  getCitas: async (page = 0, size = 10): Promise<Page<CitaResponse>> => {
    const res = await fetch(
      `${API_URL}/api/citas?page=${page}&size=${size}`,
      { headers: getAuthHeader() }
    );
    if (!res.ok) throw new Error('Failed to fetch citas');
    return res.json();
  },

  getCita: async (id: number): Promise<CitaResponse> => {
    const res = await fetch(`${API_URL}/api/citas/${id}`, {
      headers: getAuthHeader(),
    });
    if (!res.ok) throw new Error('Failed to fetch cita');
    return res.json();
  },

  createCita: async (data: CitaRequest): Promise<CitaResponse> => {
    const res = await fetch(`${API_URL}/api/citas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to create cita');
    return res.json();
  },

  updateCita: async (id: number, data: CitaRequest): Promise<CitaResponse> => {
    const res = await fetch(`${API_URL}/api/citas/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...getAuthHeader() },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error('Failed to update cita');
    return res.json();
  },

  deleteCita: async (id: number): Promise<void> => {
    const res = await fetch(`${API_URL}/api/citas/${id}`, {
      method: 'DELETE',
      headers: getAuthHeader(),
    });
    if (!res.ok) throw new Error('Failed to delete cita');
  },

  getCitasByPaciente: async (pacienteId: number): Promise<CitaResponse[]> => {
    const res = await fetch(`${API_URL}/api/citas/paciente/${pacienteId}`, {
      headers: getAuthHeader(),
    });
    if (!res.ok) throw new Error('Failed to fetch citas by paciente');
    return res.json();
  },

  getCitasByDentista: async (dentistaId: number): Promise<CitaResponse[]> => {
    const res = await fetch(`${API_URL}/api/citas/dentista/${dentistaId}`, {
      headers: getAuthHeader(),
    });
    if (!res.ok) throw new Error('Failed to fetch citas by dentista');
    return res.json();
  },

  getCitasEnRango: async (inicio: string, fin: string): Promise<CitaResponse[]> => {
    const res = await fetch(
      `${API_URL}/api/citas/rango?inicio=${inicio}&fin=${fin}`,
      { headers: getAuthHeader() }
    );
    if (!res.ok) throw new Error('Failed to fetch citas en rango');
    return res.json();
  },

  getCitasFuturasPaciente: async (pacienteId: number): Promise<CitaResponse[]> => {
    const res = await fetch(`${API_URL}/api/citas/paciente/${pacienteId}/futuras`, {
      headers: getAuthHeader(),
    });
    if (!res.ok) throw new Error('Failed to fetch citas futuras');
    return res.json();
  },
};
```

---

## Ejecución del Backend

```bash
# Iniciar con Docker
docker-compose up -d

# Ver logs
docker logs clinica_app -f

# Crear usuario admin inicial
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123","rol":"ROLE_ADMIN"}'
```

---

## Stack Recomendado para Frontend React

- **React 18+** con TypeScript
- **React Router** para navegación
- **TanStack Query (React Query)** para manejo de estado del servidor
- **Axios** o **fetch** para HTTP
- **Tailwind CSS** o **Material UI** para estilos
- **React Hook Form** + **Zod** para formularios
- **Zustand** o **Context API** para estado global (auth)
