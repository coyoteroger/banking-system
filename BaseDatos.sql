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


