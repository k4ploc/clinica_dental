.PHONY: help up down dev prod logs logs-app logs-db shell-app shell-db status health build rebuild clean test push

# Docker Helper Makefile para Clínica API
# Uso: make [target]

help:
	@echo "=================================="
	@echo "  Docker Helper - Clínica API"
	@echo "=================================="
	@echo ""
	@echo "INICIO/PARADA:"
	@echo "  make up         Inicia servicios (desarrollo con JAR)"
	@echo "  make dev        Inicia con Maven (hot-reload)"
	@echo "  make prod       Inicia producción"
	@echo "  make down       Detiene servicios"
	@echo ""
	@echo "MONITOREO:"
	@echo "  make status     Estado de servicios"
	@echo "  make logs       Ver logs en vivo"
	@echo "  make logs-app   Logs de la aplicación"
	@echo "  make logs-db    Logs de la base de datos"
	@echo "  make health     Verifica salud de servicios"
	@echo "  make stats      Estadísticas de uso"
	@echo ""
	@echo "ADMINISTRACIÓN:"
	@echo "  make shell-app  Abre bash en la aplicación"
	@echo "  make shell-db   Abre psql en PostgreSQL"
	@echo "  make build      Construye imagen Docker"
	@echo "  make rebuild    Limpia y reconstruye todo"
	@echo "  make clean      Elimina contenedores y volúmenes"
	@echo ""
	@echo "TESTING:"
	@echo "  make test       Ejecuta tests con Maven"
	@echo ""
	@echo "REGISTRY:"
	@echo "  make push       Push de imagen a registry"
	@echo ""

up:
	@echo "[*] Iniciando servicios de desarrollo..."
	docker-compose up -d
	@echo "[+] Servicios iniciados"
	@echo "[*] Esperando inicialización..."
	sleep 10
	@make status

down:
	@echo "[*] Deteniendo servicios..."
	docker-compose down
	@echo "[+] Servicios detenidos"

dev:
	@echo "[*] Iniciando con Maven (hot-reload)..."
	docker-compose -f docker-compose.dev.yml up -d
	@echo "[+] Servicios de desarrollo iniciados"
	sleep 10
	@make status

prod:
	@echo "[*] Iniciando configuración de producción..."
	docker-compose -f docker-compose.prod.yml up -d
	@echo "[+] Servicios de producción iniciados"
	sleep 10
	@make status

logs:
	docker-compose logs -f

logs-app:
	docker-compose logs -f app

logs-db:
	docker-compose logs -f db

status:
	@echo ""
	@echo "=== Estado de Servicios ==="
	docker-compose ps
	@echo ""
	@echo "=== Acceso a Aplicación ==="
	@echo "  URL: http://localhost:8080"
	@echo "  Swagger: http://localhost:8080/swagger-ui.html"
	@echo "  Health: http://localhost:8080/actuator/health"
	@echo ""

shell-app:
	@echo "[*] Abriendo shell en contenedor app..."
	docker-compose exec app bash

shell-db:
	@echo "[*] Abriendo psql en PostgreSQL..."
	docker-compose exec db psql -U postgres -d clinica_db

health:
	@echo "[*] Verificando estado de salud..."
	@echo ""
	@echo "=== Contenedores ==="
	docker-compose ps
	@echo ""
	@echo "=== Health Check (app) ==="
	@docker-compose exec app wget --quiet --tries=1 --spider http://localhost:8080/actuator/health && echo "[OK] Aplicación saludable" || echo "[ERROR] Aplicación con problemas"
	@echo ""
	@echo "=== Health Check (db) ==="
	@docker-compose exec db pg_isready -U postgres && echo "[OK] Base de datos saludable" || echo "[ERROR] Base de datos con problemas"
	@echo ""

stats:
	docker stats

build:
	@echo "[*] Construyendo imagen..."
	docker build -t clinica:latest .
	@echo "[+] Imagen construida"

rebuild:
	@echo "[*] Reconstruyendo todo..."
	docker-compose down -v
	docker build -t clinica:latest .
	docker-compose up -d
	@echo "[+] Reconstrucción completada"

clean:
	@echo "[!] Advertencia: Esto eliminará contenedores y volúmenes"
	@read -p "¿Continuar? (s/n): " confirm; \
	if [ "$$confirm" = "s" ]; then \
		docker-compose down -v; \
		echo "[+] Limpieza completada"; \
	else \
		echo "[*] Cancelado"; \
	fi

test:
	@echo "[*] Ejecutando tests..."
	docker-compose -f docker-compose.test.yml up --abort-on-container-exit

push:
	@echo "[*] Preparando push a registry..."
	@read -p "Ingrese el nombre del registry (ej: myregistry.azurecr.io): " registry; \
	docker tag clinica:latest $$registry/clinica:latest; \
	docker push $$registry/clinica:latest; \
	echo "[+] Push completado"
