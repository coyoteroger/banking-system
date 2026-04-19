-- ============================================================
-- BaseDatos.sql
-- Script compatible con PostgreSQL
-- Sistema Bancario - Microservicios: Clientes y Cuentas
-- Base de datos única: bankingdb
-- ============================================================



CREATE TABLE IF NOT EXISTS person (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    gender          VARCHAR(50)  NOT NULL,   -- MASCULINO | FEMENINO
    age             INTEGER      NOT NULL,
    identification  VARCHAR(50)  NOT NULL UNIQUE,
    address         VARCHAR(255) NOT NULL,
    phone           VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
    id          BIGINT       PRIMARY KEY REFERENCES person(id) ON DELETE CASCADE,
    customer_id VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,        -- BCrypt encoded
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS account (
    account_number  VARCHAR(50)    PRIMARY KEY,
    account_type    VARCHAR(50)    NOT NULL,   -- AHORROS | CORRIENTE
    initial_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status          BOOLEAN        NOT NULL DEFAULT TRUE,
    customer_id     VARCHAR(50)    NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movement (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    date           TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    movement_type  VARCHAR(50)    NOT NULL,
    value          NUMERIC(15,2)  NOT NULL,
    balance        NUMERIC(15,2)  NOT NULL,
    account_number VARCHAR(50)    NOT NULL REFERENCES account(account_number) ON DELETE CASCADE
);

-- ============================================================
-- INDICES
-- ============================================================
CREATE INDEX IF NOT EXISTS idx_customer_customer_id ON customer(customer_id);
CREATE INDEX IF NOT EXISTS idx_account_customer_id ON account(customer_id);
CREATE INDEX IF NOT EXISTS idx_movement_account_number ON movement(account_number);
CREATE INDEX IF NOT EXISTS idx_movement_date ON movement(date);

-- ============================================================
-- TABLA: customer_info (Caché de clientes en account-service)
-- Poblada vía eventos RabbitMQ desde customer-service
-- ============================================================
CREATE TABLE IF NOT EXISTS customer_info (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id VARCHAR(50)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    status      BOOLEAN      NOT NULL DEFAULT TRUE,
    modified_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customer_info_customer_id ON customer_info(customer_id);

-- ============================================================
-- DATOS DE PRUEBA (opcional - descomentar para seed)
-- ============================================================

-- Personas / Clientes
-- INSERT INTO person (name, gender, age, identification, address, phone)
-- VALUES ('Jose Lema', 'MALE', 30, '1234567890', 'Otavalo sn y principal', '098254785');
-- INSERT INTO customer (id, customer_id, password, status)
-- VALUES (1, 'CL000001', '$2a$10$JsIlgKEBMbqFcl/n2bPfLO4EdZJHiaaZj1SmxlF7kj76KwwvReeTq', true);  -- password: 1234

-- INSERT INTO person (name, gender, age, identification, address, phone)
-- VALUES ('Marianela Montalvo', 'FEMALE', 25, '0987654321', 'Amazonas y NNUU', '097548965');
-- INSERT INTO customer (id, customer_id, password, status)
-- VALUES (2, 'CL000002', '$2a$10$MAgM4gLgAImREGVbgkveM.EdD84tjFF3mFCG79Ufxpm3Y/y1iW/Nm', true);  -- password: 5678

-- INSERT INTO person (name, gender, age, identification, address, phone)
-- VALUES ('Juan Osorio', 'Male', 35, '1122334455', '13 junio y Equinoccial', '098874587');
-- INSERT INTO customer (id, customer_id, password, status)
-- VALUES (3, 'CL000003', '$2a$10$s70Vb4ALKMorHUDocyegsumMLtCkaSRjaEPrN3GtqTRx7YG9xC9pi', true);  -- password: 1245

-- Cuentas
-- INSERT INTO account (account_number, account_type, initial_balance, status, customer_id)
-- VALUES ('478758', 'Ahorros', 2000.00, true, 'CL000001');

-- INSERT INTO account (account_number, account_type, initial_balance, status, customer_id)
-- VALUES ('225487', 'Corriente', 100.00, true, 'CL000002');

-- INSERT INTO account (account_number, account_type, initial_balance, status, customer_id)
-- VALUES ('495878', 'Ahorros', 0.00, true, 'CL000003');

-- INSERT INTO account (account_number, account_type, initial_balance, status, customer_id)
-- VALUES ('496825', 'Ahorros', 540.00, true, 'CL000002');

-- INSERT INTO account (account_number, account_type, initial_balance, status, customer_id)
-- VALUES ('585545', 'Corriente', 1000.00, true, 'CL000001');
