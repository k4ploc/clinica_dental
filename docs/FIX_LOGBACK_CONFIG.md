# Corrección de Configuración Logback

## Problema
Al iniciar la aplicación Spring Boot con Docker Compose, se presentaba el siguiente error:

```
ERROR in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - Compression is not supported in prudent mode. Aborting
ERROR in ch.qos.logback.core.rolling.RollingFileAppender[ERROR_FILE] - Compression is not supported in prudent mode. Aborting
```

## Causa
La configuración de `logback-spring.xml` estaba usando `RollingFileAppender` sin desactivar explícitamente el modo "prudent" (sensato), lo que causaba conflicto con la compresión de archivos de log.

## Solución
Se modificó el archivo `src/main/resources/logback-spring.xml` para agregar `<prudent>false</prudent>` a los appenders:

### Cambios Realizados

#### Archivo: `src/main/resources/logback-spring.xml`

**Antes:**
```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE}</file>
    <encoder>
        <pattern>${LOG_PATTERN}</pattern>
        <charset>UTF-8</charset>
    </encoder>
    ...
</appender>
```

**Después:**
```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE}</file>
    <prudent>false</prudent>
    <encoder>
        <pattern>${LOG_PATTERN}</pattern>
        <charset>UTF-8</charset>
    </encoder>
    ...
</appender>
```

## Resultado
✅ La aplicación ahora inicia correctamente sin errores de configuración Logback
✅ Los logs se escriben sin problemas en los archivos
✅ Docker Compose levanta ambos contenedores (PostgreSQL y Clinica App) en estado "healthy"

## Verificación
- Estado de los contenedores: `docker ps` ✅
- Endpoint de health: `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` ✅
- Base de datos conectada y operativa ✅

## Comandos para Reproducir la Solución

```powershell
# Limpiar contenedores anteriores
docker-compose down

# Reconstruir la imagen con los cambios
mvn clean package -DskipTests
docker build -t clinica:latest .

# Iniciar los contenedores
docker-compose up -d

# Verificar estado
docker ps
curl http://localhost:8080/actuator/health
```

## Referencias
- [Logback Configuration Documentation](https://logback.qos.ch/)
- [Spring Boot Logging](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging)
