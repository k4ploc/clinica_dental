# Clinica API

API REST para gestión de clínica dental desarrollada con Spring Boot 3.5.5 y Java 21.

## Tecnologías

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| Java | 21 LTS | Lenguaje de programación |
| Spring Boot | 3.5.5 | Framework principal |
| Spring Security | 6.x | Autenticación JWT |
| Spring Data JPA | 3.x | Persistencia de datos |
| PostgreSQL | 18.1 | Base de datos |
| Flyway | - | Migraciones de BD |
| Lombok | - | Reducción de boilerplate |
| SpringDoc OpenAPI | 2.8.4 | Documentación Swagger |

## Estructura del Proyecto

```
src/main/java/com/clinica/
├── config/                 # Configuraciones (Security, JWT, OpenAPI)
├── controller/             # Controladores REST
├── dto/                    # Objetos de transferencia
├── errors/                 # Excepciones personalizadas
├── model/                  # Entidades JPA
│   ├── dto/               # DTOs de request/response
│   └── enums/             # Enumeraciones
├── repository/             # Repositorios JPA
└── service/                # Lógica de negocio
```

## Requisitos Previos

- Java 21+
- Maven 3.9+
- Docker y Docker Compose (opcional)

## Inicio Rápido

### Opción 1: Docker Compose (Recomendado)

```bash
# Clonar el repositorio
git clone <repository-url>
cd clinica

# Crear archivo .env
cp .env.example .env

# Iniciar servicios
docker-compose up -d
```

### Opción 2: Ejecución Local

```bash
# Configurar variables de entorno
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/clinica_db
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=tu_password

# Compilar y ejecutar
mvn clean install
mvn spring-boot:run
```

## Configuración

### Variables de Entorno

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `POSTGRES_USER` | Usuario de PostgreSQL | `admin` |
| `POSTGRES_PASSWORD` | Contraseña de PostgreSQL | `password` |
| `POSTGRES_DB` | Nombre de la base de datos | `clinica_db` |
| `SPRING_DATASOURCE_URL` | URL JDBC | `jdbc:postgresql://db:5432/clinica_db` |

### Archivo .env

```env
POSTGRES_USER=admin
POSTGRES_PASSWORD=your_secure_password
POSTGRES_DB=clinica_db
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=your_secure_password
```

## API Endpoints

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión y obtener token JWT |
| POST | `/api/auth/validate` | Validar token JWT |

### Dentistas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/dentista` | Listar dentistas (paginado) |
| GET | `/dentista/{id}` | Obtener dentista por ID |
| POST | `/dentista` | Crear nuevo dentista |
| PUT | `/dentista/{id}` | Actualizar dentista |
| DELETE | `/dentista/{id}` | Eliminar dentista |

### Pacientes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/pacientes` | Listar pacientes (paginado) |
| GET | `/pacientes/{id}` | Obtener paciente por ID |
| POST | `/pacientes` | Crear nuevo paciente |
| PUT | `/pacientes/{id}` | Actualizar paciente |
| DELETE | `/pacientes/{id}` | Eliminar paciente |

## Autenticación JWT

### Obtener Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

Respuesta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Usar Token

```bash
curl -X GET http://localhost:8080/dentista \
  -H "Authorization: Bearer <tu-token>"
```

## Paginación

Los endpoints de listado soportan paginación con los siguientes parámetros:

| Parámetro | Default | Descripción |
|-----------|---------|-------------|
| `page` | 0 | Número de página (0-indexed) |
| `size` | 10 | Elementos por página |
| `sort` | id | Campo de ordenamiento |
| `direction` | asc | Dirección (asc/desc) |

Ejemplo:
```bash
curl "http://localhost:8080/dentista?page=0&size=10&sort=nombre&direction=asc" \
  -H "Authorization: Bearer <token>"
```

## Modelos de Datos

### Dentista

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "telefono": "123456789",
  "especialidad": "ORTODONCISTA"
}
```

Especialidades disponibles: `DENTISTA`, `CIRUJANO`, `ORTODONCISTA`

### Paciente

```json
{
  "id": 1,
  "nombre": "María",
  "apellido": "García",
  "telefono": "987654321",
  "email": "maria@email.com",
  "dentistaId": 1
}
```

## Documentación API

Swagger UI disponible en: `http://localhost:8080/swagger-ui.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Endpoints de Actuator expuestos:
- `/actuator/health` - Estado de la aplicación
- `/actuator/info` - Información de la aplicación
- `/actuator/metrics` - Métricas

## Base de Datos

### Migraciones Flyway

Las migraciones se encuentran en `src/main/resources/db/migration/`:

| Migración | Descripción |
|-----------|-------------|
| V1 | Crear tabla dentista |
| V2 | Crear tabla paciente |
| V3 | Crear tabla cita |
| V4 | Agregar índices |
| V5 | Agregar timestamps |

### Diagrama ER

```
┌─────────────┐       ┌─────────────┐
│  Dentista   │       │  Paciente   │
├─────────────┤       ├─────────────┤
│ id          │───┐   │ id          │
│ nombre      │   │   │ nombre      │
│ apellido    │   │   │ apellido    │
│ telefono    │   │   │ telefono    │
│ especialidad│   │   │ email       │
│ createdAt   │   └──►│ dentista_id │
│ updatedAt   │       │ createdAt   │
└─────────────┘       │ updatedAt   │
                      └─────────────┘
```

## Docker

### Comandos Útiles

```bash
# Iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Detener servicios
docker-compose down

# Reconstruir imagen
docker-compose build --no-cache

# Limpiar volúmenes
docker-compose down -v
```

### Perfiles Disponibles

- `docker-compose.yml` - Desarrollo
- `docker-compose.prod.yml` - Producción
- `docker-compose.test.yml` - Testing

## Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar con cobertura
mvn test jacoco:report
```

## Logging

Los logs se almacenan en `logs/`:
- `clinica.log` - Logs generales
- `clinica-error.log` - Solo errores

Configuración: Rotación automática, retención 30 días, máximo 10MB por archivo.

## Desarrollo

### Compilar

```bash
mvn clean compile
```

### Ejecutar en modo desarrollo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Generar JAR

```bash
mvn clean package -DskipTests
java -jar target/clinica-0.0.1-SNAPSHOT.jar
```

## Licencia

Este proyecto es de uso privado.
