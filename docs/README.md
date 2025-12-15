# 📋 Documentación del Proyecto Clínica

## 📑 Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Arquitectura](#arquitectura)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Tecnologías Utilizadas](#tecnologías-utilizadas)
5. [Instalación y Configuración](#instalación-y-configuración)
6. [Guía de Uso](#guía-de-uso)
7. [API Endpoints](#api-endpoints)
8. [Base de Datos](#base-de-datos)
9. [Seguridad](#seguridad)
10. [Consideraciones y Mejoras](#consideraciones-y-mejoras)

---

## 📝 Descripción General

**Clínica** es una aplicación web backend desarrollada con **Spring Boot** para gestionar dentistas, pacientes y citas en una clínica dental. La aplicación proporciona APIs RESTful para crear, consultar y eliminar registros de profesionales y pacientes, facilitando la administración de la clínica.

**Versión:** 0.0.1-SNAPSHOT  
**Nombre del Proyecto:** com.clinica  
**Java Version:** 25  
**Spring Boot Version:** 3.5.5

---

## 🏗️ Arquitectura

La aplicación sigue la **arquitectura en capas** (Layered Architecture):

```
┌─────────────────────────────────────────┐
│      Controllers (REST Endpoints)        │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      Services (Business Logic)          │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      Repositories (Data Access)         │
└─────────────────────┬───────────────────┘
                      │
┌─────────────────────▼───────────────────┐
│      Database (PostgreSQL)              │
└─────────────────────────────────────────┘
```

### Capas del Proyecto:

| Capa | Responsabilidad |
|------|-----------------|
| **Controller** | Maneja solicitudes HTTP y respuestas |
| **Service** | Contiene la lógica de negocio |
| **Repository** | Acceso a la base de datos |
| **Model** | Entidades y DTOs |
| **Config** | Configuraciones de seguridad y excepciones globales |

---

## 📁 Estructura del Proyecto

```
clinica/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/clinica/
│   │   │       ├── ClinicaApplication.java          # Punto de entrada
│   │   │       ├── config/
│   │   │       │   ├── SecurityConfig.java           # Configuración de seguridad
│   │   │       │   └── GlobalExceptionHandler.java   # Manejo de excepciones
│   │   │       ├── controller/
│   │   │       │   ├── DentistaController.java       # API de Dentistas
│   │   │       │   └── PacienteController.java       # API de Pacientes
│   │   │       ├── service/
│   │   │       │   ├── DentistaService.java          # Lógica de Dentistas
│   │   │       │   └── PacienteService.java          # Lógica de Pacientes
│   │   │       ├── repository/
│   │   │       │   ├── DentistaRepository.java       # Acceso a Dentistas
│   │   │       │   └── PacienteRepository.java       # Acceso a Pacientes
│   │   │       ├── model/
│   │   │       │   ├── Dentista.java                 # Entidad Dentista
│   │   │       │   ├── Paciente.java                 # Entidad Paciente
│   │   │       │   ├── Cita.java                     # Modelo Cita
│   │   │       │   ├── dto/                          # Data Transfer Objects
│   │   │       │   │   ├── DentistaRequest.java
│   │   │       │   │   ├── DentistaResponse.java
│   │   │       │   │   ├── PacienteRequest.java
│   │   │       │   │   └── PacienteResponse.java
│   │   │       │   └── enums/
│   │   │       │       ├── Especialidad.java         # Enum de especialidades
│   │   │       │       └── Rol.java                  # Enum de roles
│   │   │       └── errors/
│   │   │           └── DuplicateException.java       # Excepción personalizada
│   │   └── resources/
│   │       ├── application.properties                # Propiedades de la app
│   │       ├── db/migration/                         # Scripts de Flyway
│   │       │   ├── V1__create_dentista_table.sql
│   │       │   ├── V2__create_paciente_table.sql
│   │       │   └── V3__create_cita_table.sql
│   │       ├── static/                               # Archivos estáticos
│   │       └── templates/                            # Templates (si aplica)
│   └── test/
│       └── java/
│           └── com/clinica/
│               └── ClinicaApplicationTests.java
├── Dockerfile                                         # Configuración Docker
├── docker-compose.yml                                 # Orquestación de contenedores
├── pom.xml                                            # Configuración de Maven
└── docs/                                              # Documentación del proyecto
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| **Java** | 25 | Lenguaje de programación |
| **Spring Boot** | 3.5.5 | Framework web |
| **Spring Security** | 3.5.5 | Autenticación y autorización |
| **Spring Data JPA** | 3.5.5 | ORM para persistencia de datos |
| **PostgreSQL** | 15 | Base de datos relacional |
| **Flyway** | - | Migrations de BD |
| **Lombok** | - | Generación de código boilerplate |
| **Maven** | 3.9.9 | Gestor de dependencias y build |
| **Docker** | - | Containerización |
| **Validation** | 3.5.5 | Validación de datos |

---

## 🚀 Instalación y Configuración

### Requisitos Previos

- **Docker** y **Docker Compose** instalados
- **Java 25** (si se ejecuta localmente)
- **Maven 3.9.9** (si se compila localmente)
- **PostgreSQL 15** (si se ejecuta sin Docker)

### Método 1: Usando Docker Compose (Recomendado)

1. **Clonar o descargar el repositorio:**
   ```bash
   cd clinica
   ```

2. **Crear archivo `.env` en la raíz del proyecto:**
   ```env
   # Base de datos
   POSTGRES_USER=clinica_user
   POSTGRES_PASSWORD=clinica_pass_123
   POSTGRES_DB=clinica_db
   
   # Spring
   SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db
   SPRING_DATASOURCE_USERNAME=clinica_user
   SPRING_DATASOURCE_PASSWORD=clinica_pass_123
   ```

3. **Ejecutar Docker Compose:**
   ```bash
   docker-compose up -d
   ```

4. **Verificar que los contenedores están corriendo:**
   ```bash
   docker-compose ps
   ```

5. **Acceder a la aplicación:**
   - URL: `http://localhost:9090`
   - Base de datos: `postgresql://localhost:5432/clinica_db`

### Método 2: Ejecución Local

1. **Instalar dependencias:**
   ```bash
   mvn clean install
   ```

2. **Configurar variables de entorno:**
   ```bash
   export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/clinica_db
   export SPRING_DATASOURCE_USERNAME=clinica_user
   export SPRING_DATASOURCE_PASSWORD=clinica_pass_123
   ```

3. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

4. **La aplicación estará disponible en:** `http://localhost:8080`

---

## 📖 Guía de Uso

### Inicio de la Aplicación

1. Asegurar que PostgreSQL esté corriendo
2. Las migraciones de Flyway se ejecutarán automáticamente al iniciar
3. La aplicación creará las tablas necesarias en la base de datos

### Estructura de Respuestas

La aplicación devuelve respuestas en formato **JSON**:

#### Respuesta de Éxito:
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "García",
  ...
}
```

#### Respuesta de Error (Validación):
```json
{
  "nombre": "Nombre es obligatorio",
  "email": "Email inválido"
}
```

#### Respuesta de Error (Conflicto):
```json
{
  "error": "El email ya se registro"
}
```

---

## 🔌 API Endpoints

### 🦷 Dentistas

#### 1. Obtener todos los dentistas
```http
GET /dentista
```
**Respuesta (200 OK):**
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
      }
    ]
  }
]
```

#### 2. Crear un nuevo dentista
```http
POST /dentista
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "García",
  "telefono": "3001234567",
  "especialidad": "ORTODONCISTA"
}
```
**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "García",
  "telefono": "3001234567",
  "especialidad": "ORTODONCISTA",
  "pacientes": null
}
```

---

### 👥 Pacientes

#### 1. Obtener todos los pacientes
```http
GET /pacientes
```
**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Carlos",
    "apellido": "López",
    "telefono": "3109876543",
    "email": "carlos@example.com"
  }
]
```

#### 2. Crear un nuevo paciente
```http
POST /pacientes
Content-Type: application/json

{
  "nombre": "Carlos",
  "apellido": "López",
  "telefono": "3109876543",
  "email": "carlos@example.com",
  "id_dentista": 1
}
```
**Respuesta (200 OK):**
```
✅ Paciente creado con ID: 
```

#### 3. Obtener paciente por ID
```http
GET /pacientes/{id}
```
**Estado:** Implementación pendiente

#### 4. Eliminar un paciente
```http
DELETE /pacientes/{id}
```
**Respuesta (200 OK):**
```
🗑️ Paciente eliminado con ID: {id}
```

---

## 🗄️ Base de Datos

### Diagrama Entidad-Relación

```
┌──────────────────┐         ┌──────────────────┐
│    DENTISTA      │         │    PACIENTE      │
├──────────────────┤         ├──────────────────┤
│ id (PK)          │────────▶│ id (PK)          │
│ nombre           │   1:N   │ nombre           │
│ apellido         │         │ apellido         │
│ telefono         │         │ telefono         │
│ especialidad     │         │ email (UNIQUE)   │
│                  │         │ dentista_id (FK) │
└──────────────────┘         └──────────────────┘
        ▲
        │
        │ 1:N
        │
┌──────────────────┐
│      CITA        │
├──────────────────┤
│ id (PK)          │
│ fecha            │
│ motivo           │
│ paciente_id (FK) │
│ dentista_id (FK) │
└──────────────────┘
```

### Tablas de la Base de Datos

#### 1. Tabla `dentista`
```sql
CREATE TABLE dentista (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    especialidad VARCHAR(100) NOT NULL
);
```

#### 2. Tabla `paciente`
```sql
CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT,
    email VARCHAR(150) UNIQUE,
    telefono VARCHAR(30),
    dentista_id INT NOT NULL,
    CONSTRAINT fk_dentista
        FOREIGN KEY (dentista_id)
        REFERENCES dentista (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
```

#### 3. Tabla `cita`
```sql
CREATE TABLE cita (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    motivo VARCHAR(255),
    paciente_id INT NOT NULL,
    dentista_id INT NOT NULL,
    CONSTRAINT fk_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_dentista
        FOREIGN KEY (dentista_id)
        REFERENCES dentista (id)
        ON DELETE CASCADE
);
```

---

## 🔐 Seguridad

### Configuración de Seguridad

La seguridad está configurada en `SecurityConfig.java`:

```java
@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)           // CSRF desactivado
           .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())  // Todos pueden acceder
           .formLogin(AbstractHttpConfigurer::disable)       // Login por formulario desactivado
           .httpBasic(AbstractHttpConfigurer::disable);      // Basic Auth desactivado
        return http.build();
    }
}
```

### ⚠️ Consideraciones de Seguridad

| Aspecto | Estado | Notas |
|--------|--------|-------|
| CSRF Protection | ❌ Desactivada | En producción debe estar activada |
| Autenticación | ❌ No implementada | Se recomienda agregar JWT o OAuth2 |
| Autorización | ❌ Todos tienen acceso | Se necesita implementar roles |
| HTTPS | ❌ No configurado | Obligatorio en producción |
| Rate Limiting | ❌ No implementado | Recomendado para evitar abuso |

---

## 🔧 Consideraciones y Mejoras

### Problemas Identificados

1. **🐛 Version de Java incompatible con Docker**
   - Java version 25 no está disponible en maven:3.9.9-eclipse-temurin-25
   - **Solución:** Cambiar a Java 21 o 23 (versiones LTS/disponibles)

2. **⚠️ Falta de autenticación y autorización**
   - La seguridad está desactivada permitiendo acceso libre
   - **Solución:** Implementar JWT, OAuth2 o sesiones autenticadas

3. **🔄 GET /pacientes retorna ciclo infinito**
   - El mapeo de dentistas puede causar problemas de serialización
   - **Solución:** Ya mitigado usando DTOs sin referencias circulares

4. **📝 Métodos incompletos**
   - `GET /pacientes/{id}` retorna null
   - `POST /pacientes` no retorna ID del paciente creado
   - **Solución:** Completar implementación de estos endpoints

5. **🗄️ Migraciones sin índices**
   - Las tablas no tienen índices en campos frecuentemente consultados
   - **Solución:** Agregar índices en email y dentista_id

### Mejoras Recomendadas

#### 1. **Validación y Manejo de Errores**
- [ ] Implementar validaciones más robustas
- [ ] Crear excepciones personalizadas más específicas
- [ ] Agregar logs más detallados

#### 2. **Autenticación y Autorización**
- [ ] Implementar JWT (JSON Web Tokens)
- [ ] Crear sistema de roles (ADMIN, DENTISTA, PACIENTE)
- [ ] Proteger endpoints sensibles

#### 3. **Bases de Datos**
- [ ] Agregar índices a campos clave
- [ ] Implementar soft deletes (borrado lógico)
- [ ] Agregar timestamps (created_at, updated_at)

#### 4. **API REST**
- [ ] Completar todos los endpoints CRUD
- [ ] Implementar paginación
- [ ] Agregar filtros y búsqueda
- [ ] Versionar la API (/api/v1/)

#### 5. **Testing**
- [ ] Agregar tests unitarios
- [ ] Agregar tests de integración
- [ ] Configurar cobertura de código

#### 6. **Documentación**
- [ ] Agregar Swagger/OpenAPI
- [ ] Documentar con Javadoc
- [ ] Crear guía de desarrollo

#### 7. **DevOps**
- [ ] Configurar CI/CD (GitHub Actions, GitLab CI)
- [ ] Agregar health checks en Docker
- [ ] Implementar logging centralizado

---

## 📞 Contacto y Soporte

Para más información, consulte la documentación específica en los archivos dentro de la carpeta `docs/`.

---

## 📄 Licencia

Este proyecto está bajo licencia (especificar tipo de licencia).

---

**Última actualización:** Diciembre 2025

