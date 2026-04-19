# Banking System - Microservicios

Sistema bancario basado en microservicios desarrollado con **Java 17** y **Spring Boot 3.2.5**, diseñado siguiendo los principios de **Arquitectura Hexagonal (Clean Architecture)** y utilizando **RabbitMQ** para la comunicación asíncrona entre servicios.

- **customer-service**: Gestiona la creación y administración de clientes, y publica eventos a RabbitMQ ante cualquier cambio (CREATED, UPDATED, DELETED).
- **account-service**: Gestiona cuentas y movimientos, y consume los eventos de RabbitMQ para mantener sincronizada su tabla local `customer_info`.

Ambos microservicios se despliegan con Docker Compose junto con PostgreSQL y RabbitMQ.

---

## Autor

**Josue Henriquez**  
Java Developer

---

## Tabla de Contenidos

- [Requisitos](#requisitos)
- [Despliegue con Docker Compose](#despliegue-con-docker-compose)
- [Ejecución](#ejecución)
- [Tecnologías](#tecnologías)
- [APIs Expuestas](#apis-expuestas)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Validaciones de DTOs](#validaciones-de-dtos)
- [Códigos de Error](#códigos-de-error)
- [Pruebas](#pruebas)
- [Base de Datos](#base-de-datos)
- [Colección de Postman](#colección-de-postman)


---

## Requisitos

- **Docker** y **Docker Compose**
- **Java 17**
- **Maven** (para compilar y empaquetar los servicios)
- Conexión a internet para descargar dependencias e imágenes Docker

---

## Despliegue con Docker Compose

El archivo `docker-compose.yml` despliega los siguientes servicios:

- **PostgreSQL:** Base de datos principal.
- **RabbitMQ:** Broker de mensajería (exchange `customer.exchange`, queue `customer.queue`).
- **customer-service:** Microservicio de clientes (puerto 8081).
- **account-service:** Microservicio de cuentas y movimientos (puerto 8082).

---

## Ejecución

Clonar el repositorio desde GitHub:

```bash
git clone https://github.com/coyoteroger/banking-system.git

```

### 1. Construir Imágenes Docker

Ingresar a la carpeta raíz del proyecto y ejecutar:

```bash
docker-compose build
```

### 2. Levantar Contenedores

Para levantar los contenedores en primer plano:

```bash
docker-compose up
```

Para ejecutarlos en segundo plano:

```bash
docker-compose up -d
```

Para detener y limpiar:

```bash
docker-compose down
docker-compose down -v   # también elimina volúmenes
```

### 3. Ejecución Local (perfil `prod`)

por defecto el sistema se ejecutara en produccion


## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje |
| Spring Boot | 3.2.5 | Framework |
| Spring Data JPA | — | Persistencia |
| Spring AMQP (RabbitMQ) | — | Mensajería asíncrona |
| Spring AOP | — | Logging aspect |
| Spring Security Crypto | — | BCrypt password encoding |
| PostgreSQL | 15 | Base de datos (prod) |
| H2 | — | Base de datos (dev/test) |
| RabbitMQ | 3-management-alpine | Message broker (prod) |
| Lombok | — | Reducción de boilerplate |
| JUnit 5 + Mockito | — | Testing |
| Docker + Docker Compose | — | Contenedores |



## APIs Expuestas

### Customer Service — Puerto 8081

| Método | Endpoint | Descripción | Status |
|---|---|---|---|
| GET | `/clientes` | Listar todos los clientes | 200 |
| GET | `/clientes/{id}` | Obtener por ID | 200 / 404 |
| GET | `/clientes/buscar/{customerId}` | Buscar por customerId | 200 / 404 |
| POST | `/clientes` | Crear cliente | 201 |
| PUT | `/clientes/{id}` | Actualizar cliente | 200 |
| DELETE | `/clientes/{id}` | **Soft delete** — status=false + evento RabbitMQ | 200 |

### Account Service — Puerto 8082

| Método | Endpoint | Descripción | Status |
|---|---|---|---|
| GET | `/cuentas` | Listar todas las cuentas | 200 |
| GET | `/cuentas/{accountNumber}` | Obtener por número | 200 / 404 |
| GET | `/cuentas/cliente/{customerId}` | Cuentas de un cliente | 200 |
| POST | `/cuentas` | Crear cuenta (valida `customer_info`) | 201 |
| PUT | `/cuentas/{accountNumber}` | Actualizar cuenta | 200 |
| GET | `/movimientos` | Listar todos los movimientos | 200 |
| GET | `/movimientos/{id}` | Obtener por ID | 200 / 404 |
| GET | `/movimientos/cuenta/{accountNumber}` | Movimientos de una cuenta | 200 |
| POST | `/movimientos` | Crear movimiento (depósito/retiro) | 201 |
| GET | `/reportes?clienteId=X&fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD` | Reporte | 200 |

---

## Ejemplos de Uso

### 1. Crear un Cliente
```bash
curl -X POST http://localhost:8081/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jose Lema",
    "gender": "MASCULINO",
    "age": 30,
    "identification": "1234567890",
    "address": "Calle 1 y calle 2",
    "phone": "098254785",
    "password": "1234",
    "status": true
  }'
```
**Response (201):**
```json
{
  "item": {
    "id": 1,
    "customerId": "A1B2C3D4",
    "name": "Jose Lema",
    "gender": "MASCULINO",
    "age": 30,
    "identification": "1234567890",
    "address": "Calle 1 y calle 2",
    "phone": "098254785",
    "status": true,
    "createdAt": "17-04-2026 10:00:00"
  },
  "message": "Cliente creado con exito",
  "status": "SUCCESS"
}
```

### 2. Soft Delete de un Cliente
```bash
curl -X DELETE http://localhost:8081/clientes/1
```
**Response (200):**
```json
{
  "item": {
    "id": 1,
    "customerId": "A1B2C3D4",
    "name": "Jose Lema",
    "status": false
  },
  "message": "Cliente desactivado con exito",
  "status": "SUCCESS"
}
```
> El `account-service` recibe el evento `DELETED` y desactiva todas las cuentas del cliente.

### 3. Crear una Cuenta
```bash
curl -X POST http://localhost:8082/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "478758",
    "accountType": "AHORROS",
    "initialBalance": 2000.00,
    "status": true,
    "customerId": "A1B2C3D4"
  }'
```
**Response (201):**
```json
{
  "item": {
    "accountNumber": "478758",
    "accountType": "AHORROS",
    "initialBalance": 2000.00,
    "status": true,
    "customerId": "A1B2C3D4",
    "createdAt": "17-04-2026 10:05:00"
  },
  "message": "Cuenta creada correctamente",
  "status": "SUCCESS"
}
```

### 4. Registrar un Movimiento
```bash
curl -X POST http://localhost:8082/movimientos \
  -H "Content-Type: application/json" \
  -d '{"accountNumber": "478758", "value": 600.00}'
```
**Response (201):**
```json
{
  "item": {
    "id": 1,
    "date": "17-04-2026 10:10:00",
    "movementType": "Deposito",
    "value": 600.00,
    "balance": 2600.00,
    "accountNumber": "478758"
  },
  "message": "Movimiento creado correctamente",
  "status": "SUCCESS"
}
```

### 5. Error: Saldo No Disponible
**Response (400):**
```json
{
  "item": {
    "errorCode": "DEVSUV-0007",
    "url": "/movimientos",
    "reqMethod": "POST",
    "timestamp": "2026-04-17T10:10:00Z"
  },
  "message": "Saldo no disponible",
  "status": "BAD_REQUEST"
}
```

### 6. Generar Reporte
```bash
curl "http://localhost:8082/reportes?clienteId=A1B2C3D4&fechaInicio=2026-01-01&fechaFin=2026-12-31"
```
**Response (200):**
```json
{
  "item": {
    "customerId": "A1B2C3D4",
    "customerName": "Jose Lema",
    "accounts": [
      {
        "accountNumber": "478758",
        "accountType": "AHORROS",
        "initialBalance": 2000.00,
        "status": true,
        "movements": [
          {
            "date": "17-04-2026 10:10:00",
            "movementType": "Deposito",
            "value": 600.00,
            "balance": 2600.00,
            "status": true
          }
        ]
      }
    ]
  },
  "message": "Reporte generado correctamente",
  "status": "SUCCESS"
}
```

---

## Validaciones de DTOs

### CustomerRequestDTO
- `name`: obligatorio
- `gender`: `MASCULINO` o `FEMENINO`
- `age`: 1–120
- `identification`: obligatorio
- `address`: obligatorio
- `phone`: obligatorio
- `password`: mínimo 4 caracteres

### AccountRequestDTO
- `accountNumber`: obligatorio
- `accountType`: `AHORROS` o `CORRIENTE`
- `initialBalance`: obligatorio
- `customerId`: obligatorio (debe existir en `customer_info` con `status=true`)

### MovementRequestDTO
- `accountNumber`: obligatorio
- `value`: entre -999,999,999.99 y 999,999,999.99, distinto de 0

---

## Códigos de Error

| Código | Descripción |
|---|---|
| DEVSUV-0001 | Recurso no encontrado |
| DEVSUV-0002 | Violación de restricción de BD |
| DEVSUV-0003 | Violación de regla de negocio |
| DEVSUV-0004 | Recurso duplicado |
| DEVSUV-0005 | Error de validación de campos |
| DEVSUV-0006 | Error genérico |
| DEVSUV-0007 | Saldo insuficiente (account-service) |

---

## Pruebas

Ejecutar pruebas unitarias y de integración:

```bash
cd customer-service && mvn clean test
cd account-service && mvn clean test
```



---

## Base de Datos

Script en `BaseDatos.sql` (PostgreSQL):

| Tabla | Servicio | Descripción |
|---|---|---|
| `person` | customer | Entidad base (herencia JOINED) |
| `customer` | customer | Extiende person: customerId, password, status |
| `account` | account | Cuentas (AHORROS/CORRIENTE) |
| `movement` | account | Movimientos de cuentas |
| `customer_info` | account | Caché de clientes, poblada por RabbitMQ |

---

## Acceso a la Base de Datos

- **Host:** `localhost`
- **Puerto:** `5432`
- **Nombre de Base de Datos:** `bankingdb`
- **Usuario:** `postgres`
- **Contraseña:** `postgres`

---

## Colección de Postman

Esta colección agrupa los endpoints principales de ambos microservicios para crear, actualizar, eliminar y consultar clientes, cuentas y movimientos.

### Ejecución de la Colección

1. **Importar la Colección en Postman:**
   - Abre Postman y haz clic en “Import”.
   - Selecciona el archivo `postman/Sistema Bancario - Microservicios.postman_collection.json` ubicado en la raíz del proyecto.

2. **Asegúrate de que los microservicios estén en ejecución:**
   - Levanta los contenedores con Docker Compose (o ejecuta los servicios localmente).
   - Verifica que customer-service y account-service estén accesibles en los puertos configurados.

3. **Ejecutar las peticiones:**
   - Selecciona la petición de la lista y haz clic en “Send”.
   - Observa la respuesta en la parte inferior de Postman.

### Estructura de la Colección

La colección está organizada en secciones: **CLIENTES**, **CUENTAS**, **MOVIMIENTOS** y **REPORTES**.

