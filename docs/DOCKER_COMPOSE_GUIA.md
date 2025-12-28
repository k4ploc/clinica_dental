# 🎯 INSTRUCCIONES FINALES - Docker Compose

## ✅ PROBLEMAS RESUELTOS

```
1. ✅ Logback FileNotFoundException         → RESUELTO
2. ✅ Swagger Requiere Autenticación        → RESUELTO
3. ✅ PostgreSQL "database admin" error     → RESUELTO
```

---

## 🚀 EJECUTAR CON DOCKER COMPOSE

### Paso 1: Limpiar (Importante)
```powershell
docker-compose down -v
```

### Paso 2: Levantar servicios
```powershell
docker-compose up -d
```

### Paso 3: Esperar inicialización (30 segundos)
```powershell
# Opcional: Ver logs en tiempo real
docker-compose logs -f
```

### Paso 4: Verificar que está listo
```powershell
# Cuando veas esto en los logs:
# - "Tomcat started on port 8080"
# - "Started ClinicaApplication"
# Entonces está listo
```

---

## 🌐 ACCESAR A LA APLICACIÓN

### Swagger UI (Recomendado)
```
http://localhost:8080/swagger-ui.html
```

✅ Interfaz interactiva para probar endpoints

### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```

### Health Check
```
http://localhost:8080/actuator/health
```

---

## 🐘 VERIFICAR POSTGRESQL

### Ver servicios corriendo
```powershell
docker-compose ps
```

Debería mostrar:
```
clinica_app       ... Up (healthy)
postgres_clinica  ... Up (healthy)
```

### Conectar a PostgreSQL
```powershell
docker exec -it postgres_clinica psql -U postgres
```

### Dentro de psql - Ver bases de datos
```sql
\l

# Debería listar: clinica_db
```

### Conectar a clinica_db
```sql
\c clinica_db
```

### Ver tablas (creadas por Flyway)
```sql
\dt
```

### Salir de psql
```sql
\q
```

---

## 📋 ARCHIVOS NECESARIOS

Verificar que existen estos archivos:

```
✅ .env                      (Configuración)
✅ docker-compose.yml        (Orquestación)
✅ init-db.sql              (Inicialización BD)
✅ Dockerfile               (Imagen aplicación)
✅ pom.xml                  (Dependencias Maven)
```

---

## 🔍 SOLUCIONAR PROBLEMAS

### Si falla PostgreSQL

**Problema:** `database "admin" does not exist`

**Solución 1:** Limpiar volumen
```powershell
docker volume rm clinica_db_data
docker-compose down -v
docker-compose up -d
```

**Solución 2:** Verificar .env
```powershell
Get-Content .env | Select-String "POSTGRES_DB"

# Debe mostrar: POSTGRES_DB=clinica_db
```

### Si falla la aplicación

**Problema:** App no conecta a PostgreSQL

**Solución:** Esperar más tiempo
```powershell
# Esperar 30-60 segundos y revisar logs
docker-compose logs app
```

### Ver logs de servicios específicos

```powershell
# PostgreSQL
docker-compose logs db

# Aplicación
docker-compose logs app

# Todos
docker-compose logs -f
```

---

## 🛑 DETENER TODO

```powershell
# Detener servicios (mantiene volumen)
docker-compose stop

# Detener y remover contenedores
docker-compose down

# Detener, remover y limpiar volumen
docker-compose down -v
```

---

## 📊 ESTADO ESPERADO

### Docker Compose ps
```
NAME              STATUS           PORTS
clinica_app       Up (healthy)     0.0.0.0:8080->8080/tcp
postgres_clinica  Up (healthy)     0.0.0.0:5432->5432/tcp
```

### Logs esperados de app
```
...
Tomcat started on port 8080 (http) with context path '/'
Started ClinicaApplication in 3.968 seconds
```

### Logs esperados de PostgreSQL
```
...
database system is ready to accept connections
```

---

## ✨ RESUMEN

| Componente | URL | Estado |
|---|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html | ✅ |
| **API JSON** | http://localhost:8080/v3/api-docs | ✅ |
| **PostgreSQL** | localhost:5432 | ✅ |
| **App** | localhost:8080 | ✅ |

---

## 🎉 ¡LISTO!

```powershell
# Ejecuta esto:
docker-compose down -v
docker-compose up -d

# Espera 30 segundos
# Luego abre:
http://localhost:8080/swagger-ui.html
```

---

## 📚 DOCUMENTACIÓN ADICIONAL

- `DATABASE_FIX_QUICK.md` - Fix de PostgreSQL rápido
- `SOLUCION_DATABASE_ERROR.md` - Solución completa de BD
- `SWAGGER_QUICK_ACCESS.md` - Acceso a Swagger
- `START.md` - Punto de entrada

---

**¡A probar!** 🚀
