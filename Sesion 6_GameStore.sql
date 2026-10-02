-- SESIÓN 6 - FUNDAMENTOS DE SQL Y MYSQL
-- GameStore Escolar

CREATE DATABASE IF NOT EXISTS gamestore_db;
USE gamestore_db;

-- Tabla de estudiantes
CREATE TABLE estudiantes (
    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    puntos INT DEFAULT 0
);

-- Tabla de recompensas
CREATE TABLE recompensas (
    id_recompensa INT AUTO_INCREMENT PRIMARY KEY,
    nombre_item VARCHAR(100) NOT NULL,
    costo_puntos INT NOT NULL
);

-- Tabla de historial de canjes
CREATE TABLE historial_canjes (
    id_canje INT AUTO_INCREMENT PRIMARY KEY,
    id_estudiante INT,
    id_recompensa INT,
    fecha_canje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_estudiante) REFERENCES estudiantes(id_estudiante),
    FOREIGN KEY (id_recompensa) REFERENCES recompensas(id_recompensa)
);

-- FOREIGN KEY permite relacionar una tabla con otra
-- y garantiza que el valor utilizado exista en la tabla relacionada.

-- Inserción de estudiantes
INSERT INTO estudiantes (nombre, puntos)
VALUES 
('Carlos Mendoza', 500),
('Ana Torres', 350);

-- Inserción de recompensas
INSERT INTO recompensas (nombre_item, costo_puntos)
VALUES 
('Skin Guerrero Cyber', 150),
('Pase de Prórroga', 300);

-- Consulta de estudiantes
SELECT * FROM estudiantes;

-- Actualización de puntos
UPDATE estudiantes 
SET puntos = 600 
WHERE id_estudiante = 1;

-- Registro de un canje
INSERT INTO historial_canjes (id_estudiante, id_recompensa)
VALUES (1, 1);

-- Consulta mostrando la relación entre las tablas
SELECT 
    h.id_canje,
    e.nombre AS estudiante,
    r.nombre_item,
    r.costo_puntos,
    h.fecha_canje
FROM historial_canjes h
JOIN estudiantes e 
    ON h.id_estudiante = e.id_estudiante
JOIN recompensas r 
    ON h.id_recompensa = r.id_recompensa;
