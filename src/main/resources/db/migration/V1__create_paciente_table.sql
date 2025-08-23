CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT,
    email VARCHAR(150) UNIQUE,
    telefono VARCHAR(30)
);
