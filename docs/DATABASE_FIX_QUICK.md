# 🚀 INSTRUCCIONES RÁPIDAS - PostgreSQL Fix

## ✅ Lo que se hizo

Se corrigió el error `database "admin" does not exist` actualizando:
1. `docker-compose.yml` - Agregado script de inicialización
2. `init-db.sql` - Nuevo archivo para preparar la BD
3. `.env` - Verificado que está correcto

---

## 🎯 Cómo Probar (3 pasos)

### Paso 1: Limpiar contenedores viejos
```powershell
docker-compose down -v
```

### Paso 2: Levantar nuevamente
```powershell
docker-compose up -d
```

### Paso 3: Verificar que funciona
```powershell
# Ver logs de PostgreSQL
docker-compose logs db

# Ver logs de la aplicación
docker-compose logs app
```

---

## ✨ Resultado Esperado

Deberías ver:

```
postgres_clinica | CREATE EXTENSION
postgres_clinica | database system is ready to accept connections

clinica_app | Started ClinicaApplication
clinica_app | Tomcat started on port 8080
```

---

## 🌐 Acceder a la Aplicación

```
http://localhost:8080/swagger-ui.html
```

✅ Swagger UI con todas las APIs

---

## 🐘 Verificar PostgreSQL

### Conectar a PostgreSQL
```powershell
docker exec -it postgres_clinica psql -U postgres
```

### Ver bases de datos
```sql
\l

# Deberías ver: clinica_db en la lista
```

### Conectar a clinica_db
```sql
\c clinica_db
```

### Ver tablas
```sql
\dt

# Flyway habrá creado las tablas
```

---

## ❌ Si Sigue Fallando

### Opción 1: Ver logs completos
```powershell
docker-compose logs -f
```

### Opción 2: Reiniciar todo
```powershell
docker-compose down -v
docker-compose up -d --build
```

### Opción 3: Verificar .env
```powershell
Get-Content .env

# Debe tener: POSTGRES_DB=clinica_db
```

---

## 📋 Archivos Nuevos/Modificados

✅ `init-db.sql` - NUEVO (inicializa BD)
✅ `docker-compose.yml` - MODIFICADO (mount init-db.sql)
✅ `.env` - VERIFICADO (correctamente configurado)

---

## ✅ ¡LISTO!

Ejecuta:
```powershell
docker-compose down -v && docker-compose up -d
```

Espera 30 segundos y luego abre:
```
http://localhost:8080/swagger-ui.html
```

---

**Documentación completa:** `SOLUCION_DATABASE_ERROR.md`
