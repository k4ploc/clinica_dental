-- Tabla de usuarios para autenticación
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de roles
CREATE TABLE rol (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Tabla intermedia usuario-rol (muchos a muchos)
CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    rol_id BIGINT NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
    PRIMARY KEY (usuario_id, rol_id)
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_usuario_username ON usuario(username);
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_activo ON usuario(activo);

-- Insertar roles predeterminados
INSERT INTO rol (nombre, descripcion) VALUES
    ('ROLE_ADMIN', 'Administrador del sistema con acceso completo'),
    ('ROLE_DENTISTA', 'Dentista con acceso a gestión de pacientes y citas'),
    ('ROLE_RECEPCIONISTA', 'Recepcionista con acceso a citas y pacientes'),
    ('ROLE_PACIENTE', 'Paciente con acceso limitado a su información');
