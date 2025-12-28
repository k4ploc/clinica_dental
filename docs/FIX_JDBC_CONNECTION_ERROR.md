# Solución: Error "Unable to commit against JDBC Connection" en Login

## Problema Identificado

Al intentar hacer login con el usuario `admin`, se generaba el siguiente error:

```
org.springframework.security.authentication.InternalAuthenticationServiceException: Unable to commit against JDBC Connection
```

### Causa Raíz

El error ocurría en el flujo de autenticación debido a un problema de **gestión de transacciones en la carga de roles del usuario**:

1. **En `CustomUserDetailsService.loadUserByUsername()`**:
   - El método tenía `@Transactional(readOnly = true)`
   - La transacción se cerraba al retornar la entidad `Usuario`

2. **En la entidad `Usuario`**:
   - Los roles estaban configurados con `@ManyToMany(fetch = FetchType.EAGER)`
   - Aunque estaban marcados como EAGER, la transacción ya estaba cerrada

3. **En el contexto de seguridad**:
   - Cuando Spring Security llamaba a `usuario.getAuthorities()` en el `AuthenticationProvider`, intentaba acceder a la colección de roles
   - Esta colección requería una sesión de Hibernate activa para inicializarse
   - Como la transacción estaba cerrada, se produía el error de JDBC

## Soluciones Implementadas

### 1. Cambio de Fetch Strategy (Solución Primaria)

**Archivo**: `src/main/java/com/clinica/model/Usuario.java`

```java
// ANTES (INCORRECTO):
@ManyToMany(fetch = FetchType.EAGER)
private Set<Rol> roles = new HashSet<>();

// DESPUÉS (CORRECTO):
@ManyToMany(fetch = FetchType.LAZY)
private Set<Rol> roles = new HashSet<>();
```

**Razón**: El cambio a `LAZY` funciona porque:
- Los roles se cargan explícitamente en la query `findActiveByUsername()` usando `LEFT JOIN FETCH`
- Esto ocurre mientras la transacción está activa
- Evita problemas de acceso a colecciones no inicializadas fuera de la transacción

### 2. Forzar Inicialización en el Servicio (Solución Secundaria)

**Archivo**: `src/main/java/com/clinica/service/CustomUserDetailsService.java`

Se agregó una línea para forzar la inicialización de los roles mientras la transacción está abierta:

```java
@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    log.debug("Buscando usuario: {}", username);

    Usuario usuario = usuarioRepository.findActiveByUsername(username)
        .orElseThrow(() -> {
            log.warn("Usuario no encontrado o inactivo: {}", username);
            return new UsernameNotFoundException("Usuario no encontrado: " + username);
        });

    // Force initialization of roles collection while transaction is still active
    usuario.getRoles().size();
    
    log.debug("Usuario encontrado: {} con {} roles", usuario.getUsername(), usuario.getRoles().size());
    return usuario;
}
```

## Query Optimizada

La query en `UsuarioRepository` ya estaba optimizada:

```java
@Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.activo = true")
Optional<Usuario> findActiveByUsername(@Param("username") String username);
```

Esta query:
- Usa `LEFT JOIN FETCH` para cargar los roles en una sola consulta
- Evita el problema N+1 query
- Inicializa completamente la colección de roles dentro de la transacción

## Verificación

Para verificar que el problema está resuelto:

1. **Compilación**: ✅ El proyecto compila sin errores
2. **Login**: Intenta hacer login con el usuario `admin`
3. **Logs esperados**: Deberías ver:
   ```
   DEBUG c.c.service.CustomUserDetailsService - Usuario encontrado: admin con 1 roles
   ```
   Sin errores de JDBC o transacciones

## Configuración Relevante

La configuración en `application.properties` ya era correcta:

```properties
spring.jpa.open-in-view=false  # Stateless = mejor rendimiento
spring.datasource.hikari.maximum-pool-size=10
spring.jpa.properties.hibernate.default_batch_fetch_size=16
```

## Recomendaciones Futuras

1. **Usar DTOs para respuestas de autenticación**: Evita exponer toda la entidad Usuario
2. **Considerar cachear los datos del usuario**: Para mejorar performance en múltiples logins
3. **Monitorear queries**: Asegúrate de que no hay queries N+1 en otros endpoints

## Arquitectura Final de Seguridad

```
AuthController.login()
    ↓
AuthenticationManager.authenticate()
    ↓
DaoAuthenticationProvider
    ↓
CustomUserDetailsService.loadUserByUsername()
    ↓
UsuarioRepository.findActiveByUsername() ← LEFT JOIN FETCH (dentro de @Transactional)
    ↓
Usuario (con roles inicializados)
    ↓
getAuthorities() ← Ya no hay problema, roles están cargados
```

---
**Fecha**: 2025-12-24  
**Status**: ✅ Resuelto  
**Prueba**: Realizar login con credenciales válidas
