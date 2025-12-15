# 🐳 Guía de Deployment y Docker

## 📋 Tabla de Contenidos
1. [Docker Setup](#docker-setup)
2. [Docker Compose](#docker-compose)
3. [Configuración de Entorno](#configuración-de-entorno)
4. [Build y Deployment](#build-y-deployment)
5. [Troubleshooting](#troubleshooting)
6. [Producción](#producción)

---

## 🐳 Docker Setup

### Requisitos

- Docker >= 20.10
- Docker Compose >= 2.0
- Espacio disponible: ~2 GB

### Instalación

**Windows:**
```bash
# Descargar Docker Desktop from https://www.docker.com/products/docker-desktop
# Instalar y reiniciar el sistema
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get update
sudo apt-get install docker.io docker-compose
sudo systemctl start docker
sudo usermod -aG docker $USER
```

**macOS:**
```bash
brew install docker docker-compose
# O instalar Docker Desktop
```

---

## 🐳 Docker Compose

### Arquitectura

```
┌─────────────────────────────────────┐
│     Docker Compose (orquestador)    │
├─────────────────────────────────────┤
│                                     │
│  ┌───────────────────────────────┐  │
│  │   clinica_app (Container)     │  │
│  │  - Maven 3.9.9                │  │
│  │  - Java 24 (⚠️ Issue)         │  │
│  │  - Spring Boot 3.5.5          │  │
│  │  - Port: 9090 → 8080          │  │
│  └───────────────────────────────┘  │
│                                     │
│  ┌───────────────────────────────┐  │
│  │   postgres_clinica (DB)       │  │
│  │  - PostgreSQL 15              │  │
│  │  - Port: 5432 → 5432          │  │
│  │  - Volume: db_data            │  │
│  └───────────────────────────────┘  │
│                                     │
│  Volumes:                           │
│  - m2_repo: Cache Maven             │
│  - db_data: Datos PostgreSQL        │
│                                     │
└─────────────────────────────────────┘
```

### Archivo docker-compose.yml

**Servicios:**

1. **app** - Aplicación Spring Boot
   - Imagen: `maven:3.9.9-eclipse-temurin-24`
   - Volúmenes: 
     - Código: `.:/app`
     - Maven cache: `m2_repo:/root/.m2/repository`
   - Puertos: `9090:8080`
   - Comando: `mvn -DskipTests spring-boot:run`

2. **db** - Base de datos PostgreSQL
   - Imagen: `postgres:15`
   - Puertos: `5432:5432`
   - Volumen: `db_data:/var/lib/postgresql/data`

---

## ⚙️ Configuración de Entorno

### Archivo .env

Crear archivo `.env` en la raíz del proyecto:

```env
# ============================================
# PostgreSQL Configuration
# ============================================
POSTGRES_USER=clinica_user
POSTGRES_PASSWORD=clinica_pass_123
POSTGRES_DB=clinica_db

# ============================================
# Spring Boot Configuration
# ============================================
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db
SPRING_DATASOURCE_USERNAME=clinica_user
SPRING_DATASOURCE_PASSWORD=clinica_pass_123

# ============================================
# Application Configuration
# ============================================
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

### Variables Importantes

| Variable | Valor | Descripción |
|----------|-------|-------------|
| `POSTGRES_USER` | clinica_user | Usuario de BD |
| `POSTGRES_PASSWORD` | clinica_pass_123 | Contraseña de BD |
| `POSTGRES_DB` | clinica_db | Nombre de BD |
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://db:5432/clinica_db | URL de conexión (host es el nombre del servicio) |
| `SPRING_PROFILES_ACTIVE` | dev/prod | Perfil de la aplicación |

⚠️ **Nota:** En producción cambiar passwords a valores seguros

---

## 🚀 Build y Deployment

### Opción 1: Usando Docker Compose (Recomendado)

**1. Iniciar servicios:**
```bash
docker-compose up -d
```

**2. Ver logs:**
```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app

# Solo la base de datos
docker-compose logs -f db
```

**3. Verificar estado:**
```bash
docker-compose ps
```

**Salida esperada:**
```
NAME                COMMAND                  SERVICE     STATUS      PORTS
clinica_app         "mvn -DskipTests sp…"   app         running     0.0.0.0:9090->8080/tcp
postgres_clinica    "docker-entrypoint…"    db          running     0.0.0.0:5432->5432/tcp
```

**4. Acceder a la aplicación:**
```
http://localhost:9090
```

**5. Detener servicios:**
```bash
docker-compose down
```

**6. Limpiar todo (incluyendo volúmenes):**
```bash
docker-compose down -v
```

---

### Opción 2: Build Manual con Dockerfile

**1. Build de imagen:**
```bash
docker build -t clinica:latest .
```

**2. Ejecutar contenedor:**
```bash
docker run -d \
  --name clinica_app \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/clinica_db \
  -e SPRING_DATASOURCE_USERNAME=clinica_user \
  -e SPRING_DATASOURCE_PASSWORD=clinica_pass_123 \
  clinica:latest
```

**3. Ver logs:**
```bash
docker logs -f clinica_app
```

---

## 🔧 Troubleshooting

### Problema 1: Error - "maven:3.9.9-eclipse-temurin-25 not found"

**Síntoma:**
```
Error failed to resolve reference "docker.io/library/maven:3.9.9-eclipse-temurin-25"
```

**Causa:** La imagen Java 25 no existe en Docker Hub

**Solución 1 - Cambiar a versión disponible:**
```yaml
# En docker-compose.yml
image: maven:3.9.9-eclipse-temurin-21  # Cambiar 25 → 21 (LTS)
```

**Solución 2 - Cambiar en pom.xml:**
```xml
<!-- En pom.xml -->
<maven.compiler.release>21</maven.compiler.release>
<java.version>21</java.version>
```

---

### Problema 2: Puerto ya en uso

**Síntoma:**
```
Error: listen tcp 0.0.0.0:9090: bind: Only one usage of each socket address (protocol/IP type/IP) is normally permitted.
```

**Solución 1 - Cambiar puerto:**
```yaml
# En docker-compose.yml
ports:
  - "9091:8080"  # Cambiar 9090 → 9091
```

**Solución 2 - Liberar puerto:**
```bash
# Windows
netstat -ano | findstr :9090
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :9090
kill -9 <PID>
```

---

### Problema 3: Base de datos no conecta

**Síntoma:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Causas posibles:**
- PostgreSQL no está corriendo
- Credenciales incorrectas
- Variables de entorno no cargadas

**Soluciones:**
```bash
# 1. Verificar que PostgreSQL está corriendo
docker-compose ps

# 2. Verificar logs de BD
docker-compose logs db

# 3. Recrear servicios
docker-compose down
docker-compose up -d

# 4. Verificar variables de entorno
docker exec clinica_app printenv | grep SPRING_DATASOURCE
```

---

### Problema 4: Maven descarga lentamente

**Síntoma:** Primera compilación toma mucho tiempo

**Solución 1 - Usar caché de volumen:**
```bash
# Ya configurado en docker-compose.yml
volumes:
  m2_repo:/root/.m2/repository
```

**Solución 2 - Pre-descargar dependencias:**
```bash
docker-compose run app mvn dependency:go-offline
```

---

### Problema 5: Cambios en código no se reflejan

**Síntoma:** Modifico archivo y no se actualiza en Docker

**Solución:**
```bash
# El volumen está en delegated mode
volumes:
  - ./:/app:delegated

# Reiniciar contenedor
docker-compose restart app
```

---

## 📊 Monitoreo

### Ver uso de recursos

```bash
# Memory y CPU
docker stats clinica_app

# Detalles del contenedor
docker inspect clinica_app

# Procesos dentro del contenedor
docker top clinica_app
```

### Acceder al contenedor

```bash
# Bash en el contenedor app
docker exec -it clinica_app /bin/bash

# PostgreSQL CLI
docker exec -it postgres_clinica psql -U clinica_user -d clinica_db
```

### Base de datos

```bash
# Conectar a PostgreSQL desde host
psql -h localhost -U clinica_user -d clinica_db

# Contraseña: clinica_pass_123
```

---

## 🏭 Producción

### Dockerfile Multi-Stage (Optimizado)

El Dockerfile actual tiene dos etapas:

**Stage 1 - Builder:**
```dockerfile
FROM maven:3.9.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests
```

**Stage 2 - Runtime:**
```dockerfile
FROM eclipse-temurin:25-jre-jammy AS runtime
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
```

### Ventajas del Multi-Stage

✅ Imagen final más pequeña (sin Maven)
✅ Construcción más rápida en CI/CD
✅ Mejor seguridad (sin dependencias innecesarias)

### Configuración para Producción

1. **Crear archivo `.env.prod`:**
```env
POSTGRES_USER=prod_user
POSTGRES_PASSWORD=secure_random_password_here
POSTGRES_DB=clinica_prod

SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_prod
SPRING_DATASOURCE_USERNAME=prod_user
SPRING_DATASOURCE_PASSWORD=secure_random_password_here

SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

2. **Usar compose para producción:**
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

3. **Con Load Balancer (Nginx):**
```yaml
services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - app
```

4. **Scaling horizontal:**
```bash
docker-compose up -d --scale app=3
```

---

## 📋 Checklist de Deployment

### Pre-Deployment

- [ ] Cambiar contraseña de BD
- [ ] Configurar HTTPS/SSL
- [ ] Revisar logs de seguridad
- [ ] Ejecutar tests
- [ ] Backup de BD existente

### Deployment

- [ ] Pull última versión de código
- [ ] Build nueva imagen
- [ ] Test en staging
- [ ] Aplicar migrations de BD
- [ ] Iniciar servicios

### Post-Deployment

- [ ] Verificar health checks
- [ ] Monitorear logs
- [ ] Testear endpoints críticos
- [ ] Validar integridad de datos

---

## 🔐 Seguridad en Docker

### Best Practices

1. **No usar root:**
```dockerfile
RUN groupadd -r appgrp && useradd -r -g appgrp appuser
USER appuser
```

2. **Health Checks:**
```yaml
services:
  app:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

3. **Secrets Management:**
```bash
# Usar secrets de Docker Swarm o externos
docker secret create db_password <(echo "secure_password")
```

4. **Image Scanning:**
```bash
docker scan clinica:latest
```

---

**Última actualización:** Diciembre 2025

