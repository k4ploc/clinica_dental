-- Initialize PostgreSQL database for Clinica application
-- Este script se ejecuta automáticamente cuando PostgreSQL inicia

-- Crear extensión UUID si no existe (es la única que es necesaria)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Crear esquema si no existe
CREATE SCHEMA IF NOT EXISTS public;

-- Log para confirmar inicialización
SELECT 'Clinica database initialization completed successfully' as status;
