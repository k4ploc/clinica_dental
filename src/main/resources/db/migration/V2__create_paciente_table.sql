CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT,
    email VARCHAR(150) UNIQUE,
    telefono VARCHAR(30),
    dentista_id INT NOT NULL,
    CONSTRAINT fk_dentista
        FOREIGN KEY (dentista_id)
        REFERENCES dentista (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);