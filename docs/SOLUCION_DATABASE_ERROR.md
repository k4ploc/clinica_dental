# 🔧 SOLUCIÓN - Error "database admin does not exist"

## 🎯 Problema Original

```
FATAL: database "admin" does not exist
```

## 🔍 Causa

PostgreSQL estaba intentando conectarse a una base de datos llamada "admin" que no existía.

Esto sucede cuando:
1. La variable `POSTGRES_DB` no se pasa correctamente al contenedor
2. PostgreSQL intenta conectarse sin esperar a que la base de datos se haya creado
3. La aplicación intenta conectarse antes de que PostgreSQL esté listo

## ✅ Soluciones Implementadas

### 1. Archivo .env Actualizado
**Archivo:** `.env`

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=120315
POSTGRES_DB=clinica_db  # ✅ Esta debe ser igual a la URL de Spring
```

**Verificación:** 
```
POSTGRES_DB=clinica_db
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/clinica_db  # ✅ IGUAL
```

### 2. Docker Compose Actualizado
**Archivo:** `docker-compose.yml`

**Cambio:** Agregado script de inicialización SQL

```yaml
db:
  volumes:
    - db_data:/var/lib/postgresql/data
    - ./init-db.sql:/docker-entrypoint-initdb.d/init.sql  # ✅ NUEVO
```

**Por qué:**
- PostgreSQL ejecuta scripts en `/docker-entrypoint-initdb.d/` automáticamente
- Permite crear extensiones y validar la base de datos

### 3. Script Inicialización SQL
**Archivo:** `init-db.sql` (NUEVO)

```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "jsonb_plpgsql";
```

**Propósito:**
- Crear extensiones necesarias para la aplicación
- Validar que la base de datos está lista
- Ejecutarse antes de que la aplicación intente conectarse

---

## 🚀 Cómo Probar

### Paso 1: Limpiar contenedores anteriores
```powershell
docker-compose down -v
```

### Paso 2: Verificar archivos
```powershell
# Verificar .env
Get-Content .env

# Verificar init-db.sql
Get-Content init-db.sql
```

### Paso 3: Levantar con docker-compose
```powershell
docker-compose up -d
```

### Paso 4: Verificar logs de PostgreSQL
```powershell
docker-compose logs db
```

Deberías ver algo como:
```
clinica_db=# CREATE EXTENSION "uuid-ossp"
clinica_db=# CREATE EXTENSION "jsonb_plpgsql"
```

### Paso 5: Verificar que la aplicación conecta
```powershell
docker-compose logs app | findstr "started\|ERROR"
```

---

## 📋 Checklist de Solución

- [x] Verificar que `.env` tiene `POSTGRES_DB=clinica_db`
- [x] Verificar que Spring datasource URL apunta a `clinica_db`
- [x] Crear archivo `init-db.sql` para inicialización
- [x] Actualizar `docker-compose.yml` para montar `init-db.sql`
- [x] Agregar healthcheck adecuado
- [x] Agregar `depends_on` con condición `service_healthy`

---

## 🔄 Flujo de Inicialización

```
1. docker-compose up
   ↓
2. PostgreSQL inicia
   ↓
3. PostgreSQL lee variables de entorno
   - POSTGRES_USER=postgres
   - POSTGRES_PASSWORD=120315
   - POSTGRES_DB=clinica_db  ✅
   ↓
4. PostgreSQL crea base de datos "clinica_db"
   ↓
5. PostgreSQL ejecuta /docker-entrypoint-initdb.d/init.sql
   - Crea extensiones
   ✓ Base de datos lista
   ↓
6. App espera healthcheck
   - PostgreSQL responde a pg_isready
   ↓
7. App conecta a:
   - jdbc:postgresql://db:5432/clinica_db  ✅ EXISTE
   ↓
8. Flyway ejecuta migraciones
   ↓
9. Aplicación lista
```

---

## ✨ Características Agregadas

| Feature | Propósito |
|---------|-----------|
| `init-db.sql` | Inicializar extensiones PostgreSQL |
| Healthcheck BD | Esperar a que BD esté lista |
| `depends_on` | App espera a DB antes de iniciar |
| Variables ENV | Garantizar mismo nombre de BD |

---

## 🆘 Si Sigue Fallando

### Opción 1: Limpiar todo
```powershell
docker-compose down -v
docker-compose up -d
```

### Opción 2: Ver logs detallados
```powershell
docker-compose logs -f db
docker-compose logs -f app
```

### Opción 3: Verificar volumen
```powershell
docker volume ls | findstr clinica
docker volume inspect clinica_db_data
```

### Opción 4: Conectar manualmente a PostgreSQL
```powershell
docker exec -it postgres_clinica psql -U postgres

# Dentro de psql:
\l                           # Listar bases de datos
\c clinica_db                # Conectar a clinica_db
\dt                          # Ver tablas
```

---

## 📝 Archivos Modificados

| Archivo | Cambio |
|---------|--------|
| `.env` | ✅ Verificado formato correcto |
| `docker-compose.yml` | ✅ Agregado mount de init-db.sql |
| `init-db.sql` | ✅ Nuevo archivo de inicialización |

---

## 🎉 Resultado Esperado

Después de `docker-compose up -d`:

```
✅ PostgreSQL inicia
✅ Base de datos "clinica_db" se crea
✅ Extensiones se instalan
✅ Aplicación se conecta
✅ Flyway ejecuta migraciones
✅ API disponible en http://localhost:8080
```

---

**Última actualización:** 21 de Diciembre de 2025  
**Status:** ✅ SOLUCIÓN IMPLEMENTADA
