-- Agregar columna estado a la tabla cita
ALTER TABLE cita ADD COLUMN estado VARCHAR(20) DEFAULT 'PROGRAMADA' NOT NULL;

-- Crear indice para consultas por estado
CREATE INDEX idx_cita_estado ON cita(estado);

-- Indice compuesto para consultas de citas activas por dentista
CREATE INDEX idx_cita_dentista_estado ON cita(dentista_id, estado);

-- Indice compuesto para consultas de citas activas por paciente
CREATE INDEX idx_cita_paciente_estado ON cita(paciente_id, estado);
