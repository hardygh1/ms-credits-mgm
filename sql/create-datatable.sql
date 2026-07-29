-- ==========================================================
-- SISTEMA DE GESTIÓN DE PRÉSTAMOS - MVP
-- FASE 1: BASE DE DATOS
-- Motor: MySQL 8+
--
-- v2: catálogo de parámetros (normalización de estados y tipo de documento)
-- ==========================================================

DROP DATABASE IF EXISTS prestamo_mvp;

CREATE DATABASE prestamo_mvp
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE prestamo_mvp;

-- ==========================================================
-- TABLA: PARAMETROS
-- Catálogo genérico y reutilizable para valores que antes se
-- guardaban como texto plano (estados, tipo de documento, y
-- cualquier catálogo futuro). "tipo" agrupa el catálogo
-- (ej. ST = estados, TDOC = tipo de documento); "codigo" es el
-- valor puntual, abreviado, dentro de ese tipo (ej. PEN, DNI);
-- "descripcion" trae el texto completo legible (ej. PENDIENTE).
-- ==========================================================

CREATE TABLE parametros (

    parametro_id INT AUTO_INCREMENT PRIMARY KEY,

    tipo VARCHAR(30) NOT NULL,

    codigo VARCHAR(30) NOT NULL,

    descripcion VARCHAR(100) NOT NULL,

    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',

    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_parametro_tipo_codigo UNIQUE (tipo, codigo)
);

-- ==========================================================
-- TABLA: USUARIOS
-- ==========================================================

CREATE TABLE usuarios (
    usuario_id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    estado_id INT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_estado
        FOREIGN KEY(estado_id)
        REFERENCES parametros(parametro_id)
);

-- ==========================================================
-- TABLA: CLIENTES
-- ==========================================================

CREATE TABLE clientes (
    cliente_id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento_id INT NOT NULL,
    numero_documento VARCHAR(15) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    correo VARCHAR(150),
    estado_id INT NOT NULL,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_cliente_tipo_numero_documento UNIQUE (tipo_documento_id, numero_documento),

    CONSTRAINT fk_cliente_tipo_documento
        FOREIGN KEY(tipo_documento_id)
        REFERENCES parametros(parametro_id),

    CONSTRAINT fk_cliente_estado
        FOREIGN KEY(estado_id)
        REFERENCES parametros(parametro_id)
);

-- ==========================================================
-- TABLA: COTIZACIONES
-- ==========================================================

CREATE TABLE cotizaciones (
    cotizacion_id INT AUTO_INCREMENT PRIMARY KEY,

    cliente_id INT NOT NULL,

    monto DECIMAL(12,2) NOT NULL,

    tasa_interes DECIMAL(5,2) NOT NULL,

    plazo_meses INT NOT NULL,

    cuota_mensual DECIMAL(12,2) NOT NULL,

    total_interes DECIMAL(12,2) NOT NULL,

    total_pagar DECIMAL(12,2) NOT NULL,

    estado_id INT NOT NULL,

    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_cotizacion_cliente
        FOREIGN KEY(cliente_id)
        REFERENCES clientes(cliente_id),

    CONSTRAINT fk_cotizacion_estado
        FOREIGN KEY(estado_id)
        REFERENCES parametros(parametro_id)
);

-- ==========================================================
-- TABLA: DETALLE COTIZACION
-- ==========================================================

CREATE TABLE detalle_cotizacion (

    detalle_id INT AUTO_INCREMENT PRIMARY KEY,

    cotizacion_id INT NOT NULL,

    numero_cuota INT NOT NULL,

    fecha_pago DATE NOT NULL,

    capital DECIMAL(12,2) NOT NULL,

    interes DECIMAL(12,2) NOT NULL,

    cuota DECIMAL(12,2) NOT NULL,

    saldo DECIMAL(12,2) NOT NULL,

    CONSTRAINT fk_detalle_cotizacion
        FOREIGN KEY(cotizacion_id)
        REFERENCES cotizaciones(cotizacion_id)
        ON DELETE CASCADE
);

-- ==========================================================
-- TABLA: PRESTAMOS
-- ==========================================================

CREATE TABLE prestamos (

    prestamo_id INT AUTO_INCREMENT PRIMARY KEY,

    cotizacion_id INT NOT NULL,

    cliente_id INT NOT NULL,

    estado_id INT NOT NULL,

    fecha_aprobacion DATETIME NULL,

    fecha_desembolso DATE NULL,

    CONSTRAINT fk_prestamo_cliente
        FOREIGN KEY(cliente_id)
        REFERENCES clientes(cliente_id),

    CONSTRAINT fk_prestamo_cotizacion
        FOREIGN KEY(cotizacion_id)
        REFERENCES cotizaciones(cotizacion_id),

    CONSTRAINT fk_prestamo_estado
        FOREIGN KEY(estado_id)
        REFERENCES parametros(parametro_id)
);

-- ==========================================================
-- TABLA: CUOTAS
-- ==========================================================

CREATE TABLE cuotas (

    cuota_id INT AUTO_INCREMENT PRIMARY KEY,

    prestamo_id INT NOT NULL,

    numero_cuota INT NOT NULL,

    fecha_vencimiento DATE NOT NULL,

    capital DECIMAL(12,2) NOT NULL,

    interes DECIMAL(12,2) NOT NULL,

    monto DECIMAL(12,2) NOT NULL,

    saldo DECIMAL(12,2) NOT NULL,

    estado_id INT NOT NULL,

    CONSTRAINT fk_cuota_prestamo
        FOREIGN KEY(prestamo_id)
        REFERENCES prestamos(prestamo_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_cuota_estado
        FOREIGN KEY(estado_id)
        REFERENCES parametros(parametro_id)
);

-- ==========================================================
-- TABLA: PAGOS
-- ==========================================================

CREATE TABLE pagos (

    pago_id INT AUTO_INCREMENT PRIMARY KEY,

    cuota_id INT NOT NULL,

    fecha_pago DATETIME NOT NULL,

    monto_pagado DECIMAL(12,2) NOT NULL,

    metodo_pago VARCHAR(30) NOT NULL,

    CONSTRAINT fk_pago_cuota
        FOREIGN KEY(cuota_id)
        REFERENCES cuotas(cuota_id)
        ON DELETE CASCADE
);

-- ==========================================================
-- ÍNDICES
-- ==========================================================

CREATE INDEX idx_parametro_tipo
ON parametros(tipo);

CREATE INDEX idx_cliente_numero_documento
ON clientes(numero_documento);

CREATE INDEX idx_cliente_nombre
ON clientes(nombres);

CREATE INDEX idx_cotizacion_cliente
ON cotizaciones(cliente_id);

CREATE INDEX idx_prestamo_cliente
ON prestamos(cliente_id);

CREATE INDEX idx_cuota_prestamo
ON cuotas(prestamo_id);

CREATE INDEX idx_pago_cuota
ON pagos(cuota_id);

-- ==========================================================
-- DATOS INICIALES: PARAMETROS
-- ==========================================================

-- TIPO = ST
INSERT INTO parametros (tipo, codigo, descripcion) VALUES
    ('ST', 'ACT', 'ACTIVO'),
    ('ST', 'INA', 'INACTIVO'),
    ('ST', 'PEN', 'PENDIENTE'),
    ('ST', 'APR', 'APROBADO'),
    ('ST', 'RECH', 'RECHAZADO'),
    ('ST', 'PAG', 'PAGADO'),
    ('ST', 'ANUL', 'ANULADO');

-- TIPO = TDOC
INSERT INTO parametros (tipo, codigo, descripcion) VALUES
    ('TDOC', 'DNI', 'DNI'),
    ('TDOC', 'CE', 'CE'),
    ('TDOC', 'PAS', 'PASAPORTE'),
    ('TDOC', 'RUC', 'RUC');
