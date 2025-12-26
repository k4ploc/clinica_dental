# Coleccion Insomnia - Clinica Dental API

## Importar la Coleccion

1. Abre Insomnia
2. Ve a **Application** > **Preferences** > **Data** > **Import Data** > **From File**
3. Selecciona el archivo `clinica-api-collection.json`

## Entornos Disponibles

La coleccion incluye 3 entornos preconfigurados:

| Entorno | URL Base | Descripcion |
|---------|----------|-------------|
| **Local** | `http://localhost:8080` | Desarrollo local |
| **Docker** | `http://localhost:8080` | Contenedor Docker |
| **Production** | `https://api.tudominio.com` | Produccion (configurar) |

### Cambiar Entorno
Click en el dropdown de entorno (esquina superior izquierda) y selecciona el entorno deseado.

## Variables de Entorno

Cada entorno tiene estas variables configurables:

```
base_url   - URL base de la API
username   - Usuario para autenticacion
password   - Contrasena del usuario
```

Para editarlas: **Manage Environments** > Selecciona el entorno > Edita los valores

## Uso Automatizado del Token

La coleccion esta configurada para **obtener el token automaticamente**:

### Flujo de trabajo:

1. **Ejecuta "2. Login (Get Token)"** primero
   - Esto autentica con las credenciales del entorno
   - Guarda el token automaticamente

2. **Ejecuta cualquier otro request**
   - El token se inyecta automaticamente en el header `Authorization: Bearer <token>`
   - Usa "Response Chaining" de Insomnia

### Si el token expira:
Simplemente ejecuta "Login" de nuevo y continua trabajando.

## Estructura de Requests

```
Clinica Dental API/
|-- Auth/
|   |-- 1. Register User      POST /api/auth/register
|   |-- 2. Login (Get Token)  POST /api/auth/login
|   |-- 3. Validate Token     POST /api/auth/validate
|
|-- Dentistas/
|   |-- List Dentistas        GET    /api/dentista
|   |-- Get Dentista by ID    GET    /api/dentista/{id}
|   |-- Create Dentista       POST   /api/dentista
|   |-- Update Dentista       PUT    /api/dentista/{id}
|   |-- Delete Dentista       DELETE /api/dentista/{id}
|
|-- Pacientes/
    |-- List Pacientes        GET    /api/pacientes
    |-- Get Paciente by ID    GET    /api/pacientes/{id}
    |-- Create Paciente       POST   /api/pacientes
    |-- Update Paciente       PUT    /api/pacientes/{id}
    |-- Delete Paciente       DELETE /api/pacientes/{id}
```

## Inicio Rapido

```bash
# 1. Levantar el backend
docker-compose up -d

# 2. En Insomnia:
#    - Seleccionar entorno "Local" o "Docker"
#    - Ejecutar "1. Register User" (solo primera vez)
#    - Ejecutar "2. Login (Get Token)"
#    - Probar los demas endpoints
```

## Especialidades Validas (Dentistas)

- `ORTODONCIA`
- `ENDODONCIA`
- `PERIODONCIA`
- `CIRUGIA`
- `ODONTOPEDIATRIA`
- `GENERAL`

## Roles de Usuario

- `ROLE_ADMIN` - Acceso completo
- `ROLE_DENTISTA` - Gestion de pacientes y citas
- `ROLE_RECEPCIONISTA` - Citas y pacientes
- `ROLE_PACIENTE` - Acceso limitado

## Troubleshooting

### Error 401 Unauthorized
- Ejecuta "Login" para obtener un nuevo token
- Verifica credenciales en el entorno

### Error 403 Forbidden
- El usuario no tiene permisos para este recurso
- Registra un usuario con rol `ROLE_ADMIN`

### Error de conexion
- Verifica que el backend este corriendo
- Comprueba la URL base en el entorno
