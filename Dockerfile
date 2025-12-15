# syntax=docker/dockerfile:1.5
# ============================================================================
# 🏗️ Stage 1 — Build with Maven
# ============================================================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Cache Maven dependencies (using BuildKit mount cache)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:go-offline

# Copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests -DskipITs

# ============================================================================
# 🚀 Stage 2 — Minimal Runtime Image (alpine-based)
# ============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgrp && adduser -S appuser -G appgrp

# Copy only the built JAR from builder
COPY --from=builder /app/target/clinica-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgrp /app/app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# JVM optimization flags for containerized environment
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

