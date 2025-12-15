-- Migración V4: Agregar índices para optimizar queries
-- Fecha: 2025-12-14

-- Índices en tabla paciente
CREATE INDEX idx_paciente_email ON paciente(email);
CREATE INDEX idx_paciente_dentista_id ON paciente(dentista_id);

-- Índices en tabla cita
CREATE INDEX idx_cita_paciente_id ON cita(paciente_id);
CREATE INDEX idx_cita_dentista_id ON cita(dentista_id);
CREATE INDEX idx_cita_fecha ON cita(fecha);

-- Índice composite para búsquedas comunes
CREATE INDEX idx_cita_paciente_dentista ON cita(paciente_id, dentista_id);

