CREATE TABLE cita (
    id SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL,
    motivo VARCHAR(255),

    paciente_id INT NOT NULL,
    dentista_id INT NOT NULL,

    CONSTRAINT fk_paciente
        FOREIGN KEY (paciente_id)
        REFERENCES paciente (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_dentista
        FOREIGN KEY (dentista_id)
        REFERENCES dentista (id)
        ON DELETE CASCADE
);
