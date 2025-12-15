<!-- 
  GUÍA DE INICIO RÁPIDO - PROYECTO CLÍNICA
  Archivo: docs/QUICK_START.md
-->

# ⚡ Guía de Inicio Rápido

Bienvenido al proyecto Clínica. Esta es la forma más rápida de empezar.

---

## 🎯 Elige tu Rol

### 👨‍💻 **Soy Desarrollador Backend**

```bash
# 1. Lee la arquitectura
docs/ARCHITECTURE.md

# 2. Ejecuta localmente
docker-compose up -d

# 3. Revisa los endpoints
docs/API.md

# 4. Implementa mejoras
docs/OPTIMIZATIONS.md
```

**Tareas inmediatas:**
1. Cambiar Java 25 → 21 en `pom.xml`
2. Completar `GET /pacientes/{id}`
3. Agregar índices a BD
4. Implementar autenticación

---

### 🚀 **Soy DevOps/SRE**

```bash
# 1. Entiende Docker
docs/DEPLOYMENT.md

# 2. Ejecuta la app
docker-compose up -d

# 3. Monitorea
docker-compose logs -f app

# 4. Soluciona problemas
docs/DEPLOYMENT.md#troubleshooting
```

**Problemas comunes:**
- Java 25 no existe → Cambiar a 21
- Puerto 9090 en uso → Cambiar puerto
- BD no conecta → Ver logs

---

### 🧪 **Soy QA/Tester**

```bash
# 1. Instala la app
docs/DEPLOYMENT.md

# 2. Prueba endpoints
docs/API.md

# 3. Ejecuta casos de prueba
# GET /dentista
# POST /dentista (con validación)
# GET /pacientes
# POST /pacientes
# DELETE /pacientes/{id}

# 4. Reporta bugs
# Los criticales están en docs/OPTIMIZATIONS.md
```

---

### 🎨 **Soy Frontend Developer**

```bash
# 1. Ejecuta la API
docker-compose up -d

# 2. Consulta endpoints
docs/API.md

# 3. USA ejemplos JavaScript
# Ver sección "Ejemplos de Uso"

# 4. Integra con tu app
```

**Endpoints principales:**
- `GET /dentista` - Todos los dentistas
- `GET /pacientes` - Todos los pacientes
- `POST /dentista` - Crear dentista
- `POST /pacientes` - Crear paciente

---

### 📊 **Soy Product Manager**

```bash
# 1. Lee descripción
docs/README.md

# 2. Entiende la arquitectura
docs/ARCHITECTURE.md

# 3. Ve las mejoras
docs/OPTIMIZATIONS.md#checklist

# 4. Prioriza el roadmap
docs/OPTIMIZATIONS.md#roadmap
```

---

## 🚀 Inicio Rápido en 5 Minutos

### Paso 1: Clonar/Descargar
```bash
cd clinica
```

### Paso 2: Crear `.env`
```env
POSTGRES_USER=clinica_user
POSTGRES_PASSWORD=clinica_pass_123
POSTGRES_DB=clinica_db
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db
SPRING_DATASOURCE_USERNAME=clinica_user
SPRING_DATASOURCE_PASSWORD=clinica_pass_123
```

### Paso 3: Ejecutar Docker
```bash
docker-compose up -d
```

### Paso 4: Esperar (2-3 minutos)
Las migraciones se ejecutan automáticamente

### Paso 5: Probar
```bash
curl http://localhost:9090/dentista
```

✅ **¡Listo!**

---

## 📝 Comandos Útiles

### Ejecutar
```bash
docker-compose up -d           # Iniciar
docker-compose down            # Detener
docker-compose logs -f app     # Ver logs
docker-compose ps              # Estado
```

### Debuggear
```bash
# Conectar a la app
docker exec -it clinica_app /bin/bash

# Conectar a BD
docker exec -it postgres_clinica psql -U clinica_user -d clinica_db

# Ver recursos
docker stats clinica_app
```

### Desarrollo Local
```bash
# Sin Docker (requiere Java 21 y PostgreSQL)
mvn spring-boot:run

# Con Maven wrapper
./mvnw spring-boot:run
```

---

## 🔗 URLs Principales

| Servicio | URL |
|----------|-----|
| Aplicación | http://localhost:9090 |
| Base de datos | localhost:5432 |
| PostgreSQL CLI | `psql -h localhost -U clinica_user` |

---

## 📚 Documentación Completa

```
docs/
├── INDEX.md             ← Empieza aquí
├── README.md            ← Descripción general
├── ARCHITECTURE.md      ← Cómo funciona
├── API.md              ← Endpoints
├── DEPLOYMENT.md       ← Docker
└── OPTIMIZATIONS.md    ← Mejoras
```

---

## ⚠️ Problemas Comunes

### Error: "maven:3.9.9-eclipse-temurin-25: not found"
```bash
# Solución: Cambiar a Java 21
# En docker-compose.yml:
image: maven:3.9.9-eclipse-temurin-21
```

### Error: "Connection refused"
```bash
# Espera 30 segundos a que BD inicie
docker-compose logs db
```

### Puerto 9090 en uso
```bash
# Cambiar puerto en docker-compose.yml:
ports:
  - "9091:8080"
```

---

## ✅ Checklist de Inicio

- [ ] Descargué el proyecto
- [ ] Creé archivo `.env`
- [ ] Ejecuté `docker-compose up -d`
- [ ] Esperé 2-3 minutos
- [ ] Probé `curl http://localhost:9090/dentista`
- [ ] Leí INDEX.md
- [ ] Revisé mis tareas según mi rol

---

## 🎓 Recursos de Aprendizaje

- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Docker Docs](https://docs.docker.com/)
- [PostgreSQL Tutorial](https://www.postgresql.org/docs/15/)
- [REST API Best Practices](https://restfulapi.net/)

---

## 💬 Soporte

**Si tienes dudas:**
1. Busca en `docs/QUICK_START.md` (este archivo)
2. Consulta `docs/API.md` para endpoints
3. Lee `docs/DEPLOYMENT.md` para Docker
4. Revisa `docs/OPTIMIZATIONS.md` para problemas

---

## 📞 Contacto

Para preguntas o soporte, contacta al equipo de desarrollo.

---

**Última actualización:** Diciembre 2025

**¡Listo para empezar? → Lee `docs/INDEX.md`**

