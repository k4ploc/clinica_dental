# Logback Configuration Fix - Logging Directory Initialization

## Problem Description
The application was failing at startup with the following error:

```
java.lang.IllegalStateException: Logback configuration error detected

ERROR in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - Failed to create parent directories for [/app/logs/clinica.log]
ERROR in ch.qos.logback.core.rolling.RollingFileAppender[FILE] - openFile(logs/clinica.log,true) call failed. java.io.FileNotFoundException: logs/clinica.log (No such file or directory)
```

This occurred because the `logs/` directory did not exist when Logback tried to initialize the file appenders during application startup.

## Root Cause
- Logback initializes during Spring Boot's startup sequence
- The `RollingFileAppender` in `logback-spring.xml` was configured to write logs to `logs/clinica.log` and `logs/clinica-error.log`
- By default, Logback does NOT automatically create parent directories
- The directory needed to exist before Logback initialized

## Solutions Implemented

### 1. Enhanced Logback Configuration (`src/main/resources/logback-spring.xml`)

Added `<prudent>true</prudent>` to both file appenders:

```xml
<!-- Appender de Archivo con Rotación -->
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${LOG_FILE}</file>
    <prudent>true</prudent>
    <!-- ... rest of configuration ... -->
</appender>

<!-- Appender para Errores -->
<appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/clinica-error.log</file>
    <prudent>true</prudent>
    <!-- ... rest of configuration ... -->
</appender>
```

**Purpose of `prudent` mode:**
- Enables safer concurrent logging from multiple JVM instances
- Improves file I/O handling in containerized environments
- Better compatibility with Docker and Kubernetes deployments

### 2. Spring Component for Directory Initialization (`src/main/java/com/clinica/config/LoggingInitializer.java`)

Created a dedicated Spring component that ensures the logs directory exists during bean initialization:

```java
@Component
public class LoggingInitializer {
    private static final String LOGS_DIR = "logs";

    @PostConstruct
    public void initializeLoggingDirectory() {
        try {
            Path logsPath = Paths.get(LOGS_DIR);
            if (!Files.exists(logsPath)) {
                Files.createDirectories(logsPath);
            }
        } catch (Exception e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }
}
```

**Why this works:**
- `@Component` annotation registers the bean with Spring
- `@PostConstruct` annotation ensures the method runs during bean initialization
- Bean initialization occurs very early in Spring's lifecycle, before logging is fully attempted
- Uses `Files.createDirectories()` which creates all parent directories if needed
- Graceful error handling using `System.err` since the logger might not be initialized yet

## Technical Details

### Execution Order
1. Spring context begins initialization
2. `LoggingInitializer` bean is created
3. `@PostConstruct` method executes → logs directory is created
4. Logback initializes with configuration from `logback-spring.xml`
5. File appenders can now successfully create log files

### Java/Spring Version Compatibility
- Uses `jakarta.annotation.PostConstruct` (not `javax.annotation.PostConstruct`)
- Compatible with Java 21 and Spring Boot 3.5.5
- Uses modern Java NIO `java.nio.file` API for path operations

### Docker Compatibility
The `prudent` mode configuration is especially important for containerized deployments where:
- The `/app/logs` directory may be a mounted volume
- Multiple replicas might write to the same log destination
- File locking behavior is different in Docker/Kubernetes environments

## Files Modified

1. **src/main/resources/logback-spring.xml**
   - Added `<prudent>true</prudent>` to FILE appender
   - Added `<prudent>true</prudent>` to ERROR_FILE appender

2. **src/main/java/com/clinica/config/LoggingInitializer.java** (NEW)
   - Created new Spring component for directory initialization

## Verification Steps

To verify the fix is working:

1. **Build the project:**
   ```powershell
   mvn clean compile -DskipTests
   ```

2. **Run the application:**
   ```powershell
   mvn spring-boot:run
   ```

3. **Check that:**
   - Application starts without errors
   - `logs/` directory is created automatically
   - Log files are created and written properly
   - No FileNotFoundException errors appear

## Best Practices Applied

✅ **SOLID Principles:**
- Single Responsibility: `LoggingInitializer` has one job - ensure logs directory exists
- Separation of Concerns: Logging configuration is in XML, directory setup is in Java

✅ **Spring Boot Standards:**
- Using `@PostConstruct` for initialization logic
- Using `@Component` for automatic bean registration
- Following Spring's component lifecycle patterns

✅ **Error Handling:**
- Graceful exception handling
- Using System.err when logger isn't ready
- Non-blocking initialization

✅ **Cloud-Native Design:**
- Compatible with containerized deployments
- Proper handling of mounted volumes in Docker/Kubernetes
- Prudent mode for concurrent access scenarios

## Related Documentation

- [Logback Official Documentation](https://logback.qos.ch/)
- [Spring Boot Logging Configuration](https://docs.spring.io/spring-boot/reference/features/logging.html)
- [Jakarta EE Annotations](https://jakarta.ee/specifications/annotations/)
