# 🐳 Docker - Inicio Rápido

## ⚡ Comandos Rápidos

### Iniciar Servicios
```bash
# Desarrollo (Recomendado - JAR compilado)
docker-compose up -d

# Desarrollo con Maven (Hot-reload)
docker-compose -f docker-compose.dev.yml up -d

# Producción
docker-compose -f docker-compose.prod.yml up -d
```

### Ver Logs
```bash
docker-compose logs -f app          # Aplicación
docker-compose logs -f db           # Base de datos
docker-compose logs -f              # Todos
```

### Acceder a Servicios
```bash
# Shell en la aplicación
docker-compose exec app bash

# PostgreSQL CLI
docker-compose exec db psql -U postgres -d clinica_db

# Verificar estado
docker-compose ps
```

### Detener
```bash
docker-compose down              # Detiene servicios (datos persisten)
docker-compose down -v          # Detiene y elimina volúmenes
```

---

## 📋 Scripts Helper

### Windows (Batch)
```bash
docker-helper.bat up              # Iniciar
docker-helper.bat logs-app        # Ver logs
docker-helper.bat health          # Verificar salud
docker-helper.bat help            # Ver todos los comandos
```

### PowerShell
```bash
.\docker-helper.ps1 up
.\docker-helper.ps1 logs-app
.\docker-helper.ps1 health
.\docker-helper.ps1 help
```

---

## 🌐 URLs de Acceso

| Servicio | URL | Descripción |
|----------|-----|-------------|
| API | http://localhost:8080 | Aplicación principal |
| Swagger | http://localhost:8080/swagger-ui.html | Documentación interactiva |
| Health | http://localhost:8080/actuator/health | Estado de la aplicación |
| PostgreSQL | localhost:5432 | Base de datos |

---

## 📁 Configuración

### Variables de Entorno (Desarrollo)
```env
# .env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=120315
POSTGRES_DB=clinica_db
```

### Variables de Entorno (Producción)
```env
# .env.prod
POSTGRES_USER=clinica_user
POSTGRES_PASSWORD=CHANGE_ME_STRONG_PASSWORD
POSTGRES_DB=clinica_db
```

---

## 🔧 Troubleshooting

**Error: "Cannot connect to database"**
```bash
docker-compose restart db
docker-compose logs db
```

**Error: "Port already in use"**
```bash
# Cambiar puerto en docker-compose.yml
# ports: "8081:8080"
```

**Eliminar todo y empezar de cero**
```bash
docker-compose down -v
docker-compose up -d
```

---

## 📚 Documentación Completa

Ver [DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md) para:
- Arquitectura detallada
- Security best practices
- Despliegue en producción
- Monitoreo avanzado
- Troubleshooting completo

Ver [DOCKER_VALIDATION.md](DOCKER_VALIDATION.md) para:
- Checklist de validación
- Mejoras implementadas
- Próximos pasos

---

## ✅ Estado

✅ **Completamente dockerizado**  
✅ **Optimizado para desarrollo y producción**  
✅ **Documentado**  
✅ **Listo para CI/CD**  

---

**Para más detalles**: Ver [DOCKER_GUIDE.md](docs/DOCKER_GUIDE.md)
