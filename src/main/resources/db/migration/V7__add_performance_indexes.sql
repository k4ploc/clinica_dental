-- Índices adicionales para optimizar rendimiento

-- Índices para búsqueda en dentista
CREATE INDEX IF NOT EXISTS idx_dentista_nombre ON dentista(nombre);
CREATE INDEX IF NOT EXISTS idx_dentista_apellido ON dentista(apellido);
CREATE INDEX IF NOT EXISTS idx_dentista_especialidad ON dentista(especialidad);
CREATE INDEX IF NOT EXISTS idx_dentista_telefono ON dentista(telefono);

-- Índices para búsqueda en paciente
CREATE INDEX IF NOT EXISTS idx_paciente_nombre ON paciente(nombre);
CREATE INDEX IF NOT EXISTS idx_paciente_apellido ON paciente(apellido);
CREATE INDEX IF NOT EXISTS idx_paciente_telefono ON paciente(telefono);

-- Índice compuesto para búsqueda por nombre completo
CREATE INDEX IF NOT EXISTS idx_dentista_nombre_apellido ON dentista(nombre, apellido);
CREATE INDEX IF NOT EXISTS idx_paciente_nombre_apellido ON paciente(nombre, apellido);

-- Índices para citas
CREATE INDEX IF NOT EXISTS idx_cita_fecha ON cita(fecha);
CREATE INDEX IF NOT EXISTS idx_cita_fecha_desc ON cita(fecha DESC);
CREATE INDEX IF NOT EXISTS idx_cita_paciente_id ON cita(paciente_id);
CREATE INDEX IF NOT EXISTS idx_cita_dentista_id ON cita(dentista_id);
CREATE INDEX IF NOT EXISTS idx_cita_motivo ON cita(motivo);

-- Índice compuesto para búsqueda de citas por dentista y fecha
CREATE INDEX IF NOT EXISTS idx_cita_dentista_fecha ON cita(dentista_id, fecha);

-- Índices para usuarios (tabla de autenticación)
CREATE INDEX IF NOT EXISTS idx_usuario_activo ON usuario(activo);
CREATE INDEX IF NOT EXISTS idx_usuario_created_at ON usuario(created_at);
