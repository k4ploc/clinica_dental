# ⚡ QUICK START - JWT Authentication

## 🎯 Obtener Token en 3 Pasos

### Paso 1: Abre Swagger
```
http://localhost:8080/swagger-ui.html
```

### Paso 2: Login
1. Busca: **POST /api/auth/login**
2. Click "Try it out"
3. Rellena:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
4. Click "Execute"

### Paso 3: Copia el Token
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.ABC123..."
}
```

---

## 🔒 Usar Token en Swagger

1. Click en botón **"Authorize"** (arriba a la derecha)
2. Pega en el campo:
```
Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJBRE1JTiJdLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwMzI1MzYxOCwiZXhwIjoxNzAzMzQwMDE4fQ.ABC123...
```
3. Click "Authorize"
4. ¡Ahora todos los endpoints funcionan! ✅

---

## 👥 Usuarios Disponibles

| Usuario | Password |
|---------|----------|
| admin | admin123 |
| dentista | dentista123 |
| paciente | paciente123 |

---

## 🧪 Test con cURL

```bash
# 1. Obtener token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.access_token')

# 2. Usar token
curl -X GET http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📋 URLs Importantes

| URL | Descripción |
|-----|-------------|
| http://localhost:8080/swagger-ui.html | Swagger UI con autenticación |
| http://localhost:8080/api/auth/login | Endpoint de login |
| http://localhost:8080/api/auth/validate | Validar token |
| http://localhost:8080/api/pacientes | API protegida (ejemplo) |

---

## ✅ ¡LISTO!

**Todos los endpoints están protegidos y requieren token JWT.**

¡A usar la API! 🚀
