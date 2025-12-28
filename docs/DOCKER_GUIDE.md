# 🐳 Guía Completa de Dockerización - Clínica API

## 📋 Resumen Ejecutivo

El proyecto está **correctamente dockerizado** pero con mejoras implementadas para optimizar desarrollo y producción.

### Estado Actual:
- ✅ **Dockerfile**: Multi-stage, optimizado con caching
- ✅ **Docker Compose**: Configurado para desarrollo
- ✅ **Health Checks**: Implementados para ambos servicios
- ✅ **Network**: Configuración de red aislada
- ✅ **.dockerignore**: Creado para optimizar contexto de build

---

## 🚀 Comandos Rápidos

### **Desarrollo (Usando JAR compilado)**
```powershell
docker-compose up -d
```
- Construye la imagen del Dockerfile
- Inicia la aplicación compilada en el contenedor
- Puerto: `http://localhost:8080`

### **Desarrollo con Maven (Hot-reload)**
```powershell
docker-compose -f docker-compose.dev.yml up -d
```
- Monta el código fuente como volumen
- Ejecuta Maven directamente en el contenedor
- Permite ver cambios en vivo
- Debug remoto en puerto `5005`

### **Producción**
```powershell
docker-compose -f docker-compose.prod.yml up -d
```
- Usa imagen JAR compilada
- Configuración optimizada para producción
- Logs rotados automáticamente

---

## 📁 Estructura de Archivos Docker

```
clinica/
├── Dockerfile                 # Multi-stage build
├── docker-compose.yml         # Para desarrollo (JAR)
├── docker-compose.dev.yml     # Para desarrollo (Maven + hot-reload)
├── docker-compose.prod.yml    # Para producción
├── .dockerignore             # Archivos a ignorar en build
├── .env                      # Variables de desarrollo
├── .env.prod                 # Plantilla para producción
└── src/
    └── main/resources/
        └── application.properties
```

---

## 🏗️ Dockerfile - Multi-Stage Build

### **Stage 1: Builder (Maven)**
```dockerfile
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
```
- Compila el proyecto con Maven
- Cachea dependencias para builds más rápidos
- Genera JAR final: `clinica-0.0.1-SNAPSHOT.jar`

### **Stage 2: Runtime (Mínimo)**
```dockerfile
FROM eclipse-temurin:21-jre-alpine
```
- Imagen base ultra-ligera (~280MB vs ~600MB)
- Solo contiene el JRE (no Maven)
- Usuario no-root (`appuser`) para seguridad
- Health checks cada 30 segundos

### **Optimizaciones Aplicadas**
| Característica | Beneficio |
|---|---|
| Multi-stage | Reduce tamaño final ~50% |
| Alpine Linux | Imagen base más pequeña |
| BuildKit cache | Builds más rápidos |
| No-root user | Seguridad mejorada |
| Health checks | Orquestación confiable |

---

## 🐘 PostgreSQL en Docker

### **Configuración**
- **Versión**: PostgreSQL 15 Alpine
- **Puerto**: `5432` (default)
- **Volumen**: `db_data` (datos persistentes)
- **Health Check**: Verifica `pg_isready`

### **Variables de Entorno**
```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=120315          # ⚠️  Cambiar en producción
POSTGRES_DB=clinica_db
```

### **Datos Persistentes**
```yaml
volumes:
  db_data:/var/lib/postgresql/data
```
Los datos persisten incluso si se elimina el contenedor.

---

## 🌐 Networking

### **Red Aislada**
```yaml
networks:
  clinica_network:
    driver: bridge
```

**Ventajas:**
- Contenedores se comunican por nombre (`db`, `app`)
- Aislamiento del host
- DNS interno automático

**URLs de Conexión dentro de Docker:**
```
- API: http://app:8080
- BD: postgresql://db:5432
```

---

## 🔐 Seguridad

### **Implementaciones**
✅ Usuario no-root (`appuser`)  
✅ Red aislada (bridge)  
✅ Health checks integrados  
✅ Logging con rotación  

### **Mejoras Recomendadas para Producción**

#### 1. **Variables de Secretos**
```bash
# Usar Docker Secrets (Swarm)
echo "mi_password_segura" | docker secret create db_password -

# O con variables de entorno inyectadas
docker run --env-file .env.prod ...
```

#### 2. **Cambiar Credenciales**
Actualizar `.env.prod` con contraseñas fuertes:
```env
POSTGRES_PASSWORD=GenerateStrongPassword123!@#
```

#### 3. **Certificados SSL**
```dockerfile
# En Dockerfile
COPY certs/ /app/certs/
ENV SERVER_SSL_KEY_STORE=/app/certs/keystore.p12
```

#### 4. **Rate Limiting**
```yaml
# En docker-compose.prod.yml
labels:
  - "com.example.rate-limit=100/hour"
```

---

## 📊 Comparación de Configuraciones

| Aspecto | Desarrollo (JAR) | Desarrollo (Maven) | Producción |
|---|---|---|---|
| **Archivo** | `docker-compose.yml` | `docker-compose.dev.yml` | `docker-compose.prod.yml` |
| **Imagen** | Dockerfile compilado | Maven en contenedor | Dockerfile compilado |
| **Hot-reload** | ❌ | ✅ | ❌ |
| **Debug** | ❌ | ✅ (puerto 5005) | ❌ |
| **Tamaño** | ~350MB | ~600MB | ~350MB |
| **Tiempo start** | ~10s | ~30s | ~10s |
| **Logs** | Console | Console | Rotados |

---

## 📝 Health Checks

### **Aplicación (Spring Boot)**
```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health
```

**Parámetros:**
- `interval=30s`: Cada cuánto verificar
- `timeout=10s`: Timeout de la verificación
- `start-period=40s`: Tiempo antes de verificar (inicialización)
- `retries=3`: Fallos antes de marcar como unhealthy

### **Base de Datos (PostgreSQL)**
```yaml
healthcheck:
  test: [ "CMD-SHELL", "pg_isready -U postgres" ]
  interval: 10s
```

---

## 🔧 Troubleshooting

### **Error: "Cannot connect to database"**
```bash
# Verificar que PostgreSQL está healthy
docker-compose ps

# Ver logs
docker-compose logs db

# Reiniciar BD
docker-compose restart db
```

### **Error: "Port already in use"**
```bash
# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Host port 8081 → Container 8080
```

### **Limpiar todo**
```bash
docker-compose down -v  # -v = elimina volúmenes
docker image prune -a    # Elimina imágenes no usadas
```

---

## 🚢 Despliegue en Producción

### **Paso 1: Construir imagen**
```bash
docker build -t clinica:1.0 .
docker tag clinica:1.0 your-registry.azurecr.io/clinica:1.0
docker push your-registry.azurecr.io/clinica:1.0
```

### **Paso 2: Preparar .env.prod**
```bash
# Generar contraseña fuerte
openssl rand -base64 32 > db_password.txt

# Actualizar .env.prod
POSTGRES_PASSWORD=$(cat db_password.txt)
```

### **Paso 3: Desplegar**
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### **Paso 4: Verificar**
```bash
docker-compose ps
curl http://localhost:8080/actuator/health
```

---

## 📈 Monitoreo

### **Ver logs en vivo**
```bash
docker-compose logs -f app
docker-compose logs -f db
```

### **Estadísticas de contenedores**
```bash
docker stats
```

### **Ejecutar comandos en contenedor**
```bash
# En la aplicación
docker-compose exec app bash

# En PostgreSQL
docker-compose exec db psql -U postgres -d clinica_db
```

---

## ✅ Checklist de Validación

- [x] Dockerfile multi-stage implementado
- [x] .dockerignore creado
- [x] docker-compose.yml optimizado
- [x] docker-compose.dev.yml con Maven
- [x] docker-compose.prod.yml creado
- [x] Health checks configurados
- [x] Networking aislado
- [x] Volúmenes persistentes
- [x] Logging configurado
- [ ] Credenciales cambiadas en producción
- [ ] Certificados SSL instalados (si aplica)
- [ ] Secrets manager integrado

---

## 📚 Referencias

- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Docker](https://spring.io/guides/gs/spring-boot-docker/)
- [PostgreSQL Docker Hub](https://hub.docker.com/_/postgres)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [Multi-stage Builds](https://docs.docker.com/build/building/multi-stage/)

---

**Última actualización**: Diciembre 21, 2025  
**Estado**: ✅ Dockerización Completa y Optimizada
