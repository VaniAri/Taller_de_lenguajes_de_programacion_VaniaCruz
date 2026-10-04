CREATE DATABASE IF NOT EXISTS gamestore_db;

USE gamestore_db;

-- =====================================================
-- TABLA DE ESTUDIANTES
-- =====================================================

CREATE TABLE IF NOT EXISTS estudiantes (

    id_estudiante INT AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL,

    puntos INT NOT NULL DEFAULT 0
);

-- =====================================================
-- TABLA DE RECOMPENSAS
-- =====================================================

CREATE TABLE IF NOT EXISTS recompensas (

    id_recompensa INT AUTO_INCREMENT PRIMARY KEY,

    nombre_item VARCHAR(100) NOT NULL,

    costo_puntos INT NOT NULL
);

-- =====================================================
-- TABLA DE HISTORIAL
-- =====================================================

CREATE TABLE IF NOT EXISTS historial_canjes (

    id_canje INT AUTO_INCREMENT PRIMARY KEY,

    id_estudiante INT NOT NULL,

    id_recompensa INT NOT NULL,

    fecha_canje TIMESTAMP
        DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_estudiante)
        REFERENCES estudiantes(id_estudiante),

    FOREIGN KEY (id_recompensa)
        REFERENCES recompensas(id_recompensa)
);

-- =====================================================
-- ESTUDIANTES DE PRUEBA
-- =====================================================

INSERT INTO estudiantes (nombre, puntos)
SELECT 'Carlos Mendoza', 500
WHERE NOT EXISTS (
    SELECT 1
    FROM estudiantes
    WHERE nombre = 'Carlos Mendoza'
);

INSERT INTO estudiantes (nombre, puntos)
SELECT 'Ana Torres', 350
WHERE NOT EXISTS (
    SELECT 1
    FROM estudiantes
    WHERE nombre = 'Ana Torres'
);

-- =====================================================
-- RECOMPENSAS
-- =====================================================

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Skin Guerrero Cyber', 150
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Skin Guerrero Cyber'
);

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Pase de Prórroga', 300
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Pase de Prórroga'
);

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Cursor Personalizado', 50
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Cursor Personalizado'
);

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Insignia Dorada', 150
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Insignia Dorada'
);

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Fondo Exclusivo', 80
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Fondo Exclusivo'
);

INSERT INTO recompensas
(nombre_item, costo_puntos)
SELECT 'Bonus Sorpresa', 300
WHERE NOT EXISTS (
    SELECT 1
    FROM recompensas
    WHERE nombre_item = 'Bonus Sorpresa'
);

-- =====================================================
-- CONSULTA DE COMPROBACIÓN
-- =====================================================

SELECT *
FROM estudiantes;

SELECT *
FROM recompensas;

SELECT *
FROM historial_canjes;
