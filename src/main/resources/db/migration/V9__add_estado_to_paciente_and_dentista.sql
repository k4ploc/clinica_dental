-- Agregar campo estado a paciente para soft delete
ALTER TABLE paciente ADD COLUMN estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL;

-- Agregar campo estado a dentista para soft delete
ALTER TABLE dentista ADD COLUMN estado VARCHAR(20) DEFAULT 'ACTIVO' NOT NULL;

-- Crear índices para mejorar rendimiento de consultas por estado
CREATE INDEX idx_paciente_estado ON paciente(estado);
CREATE INDEX idx_dentista_estado ON dentista(estado);
