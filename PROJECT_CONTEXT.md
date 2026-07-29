# **PROJECT_CONTEXT.md**

# **Sistema de Gestión de Préstamos (MVP)**

## **Descripción**

Este proyecto corresponde al desarrollo de una API REST para un Sistema de Gestión de Préstamos (MVP).

El objetivo es construir una API sencilla, desacoplada y mantenible utilizando Spring Boot, que posteriormente será consumida por una aplicación Angular.

Este repositorio contiene únicamente el backend.

No existen vistas, componentes frontend ni páginas HTML dentro de este proyecto.

# **Objetivo del MVP**

El sistema permitirá administrar el flujo básico de un préstamo.

El alcance del MVP es únicamente:

- Autenticación mediante JWT.

- Registro de clientes.

- Consulta de clientes.

- Registro de préstamos.

- Consulta de préstamos.

- Consulta del detalle del préstamo.

- Aprobación de préstamos.

- Consulta de cuotas.

- Registro de pagos.

- Consulta del historial de pagos.

Cualquier funcionalidad fuera de este alcance no debe implementarse.

# **Flujo del negocio**

```
Usuario
    │
```

```
    ▼
```

1

```
Registro
    │
    ▼
Login
    │
    ▼
Registrar Cliente
    │
    ▼
Consultar Clientes
    │
    ▼
Registrar Préstamo
    │
    ▼
Consultar Préstamos
    │
    ▼
Detalle del Préstamo
    │
    ▼
Aprobar Préstamo
    │
    ▼
Consultar Cuotas
    │
    ▼
Registrar Pago
    │
    ▼
Consultar Historial de Pagos
```

# **Base de datos**

La estructura oficial de la base de datos se encuentra en:

```
create-datatable.sql
```

Este archivo es la única fuente de verdad.

Toda entidad debe construirse utilizando exactamente:

- tablas

2

- columnas

- tipos de datos

- relaciones

- claves primarias

- claves foráneas

No modificar la estructura definida.

No crear tablas nuevas.

No agregar columnas.

No cambiar nombres.

> **Nota (evolución v2):** esta restricción aplicó al modelo inicial del MVP. El modelo evolucionó deliberadamente para normalizar estados y tipo de documento mediante un catálogo de parámetros — ver sección **"Evolución del modelo — Catálogo de Parámetros"** más abajo para el detalle completo. Cualquier cambio de esquema posterior a esa evolución requiere, igual que entonces, un pedido explícito del propietario del proyecto.

# **Arquitectura**

El proyecto utiliza una arquitectura por capas.

```
src/main/java
│
├── config
├── constants
├── controller
├── dto
│     ├── request
│     └── response
├── entity
├── exception
├── handler
├── mapper
├── repository
├── security
├── service
│      └── impl
├── util
└──
 common
```

Cada capa posee una responsabilidad específica.

## **Controller**

Recibe las peticiones HTTP.

3

Valida las entradas.

Invoca al Service.

Retorna la respuesta.

No contiene lógica de negocio.

## **Service**

Contiene toda la lógica del negocio.

No accede directamente a HTTP.

No retorna entidades JPA.

## **Repository**

Acceso a datos mediante Spring Data JPA.

Extiende JpaRepository.

No contiene lógica de negocio.

## **DTO**

Toda comunicación entre cliente y servidor debe realizarse mediante DTOs.

Nunca exponer entidades JPA.

## **Mapper**

Toda conversión entre Entity y DTO debe realizarse mediante Mapper.

No realizar conversiones dentro de Controllers ni Services.

4

# **Seguridad**

La autenticación utiliza JWT.

Tecnologías:

- Spring Security

- JWT

- BCryptPasswordEncoder

- SecurityFilterChain

Los únicos endpoints públicos son:

- POST /auth/register

- POST /auth/login

Todos los demás requieren autenticación.

# **Respuesta estándar**

Toda respuesta de la API debe seguir el mismo formato.

Éxito

```
{
"success":true,
""
"message":,
"data":{}
}
```

Error

```
{
"success":false,
"message":""
}
```

No responder objetos distintos.

5

# **Manejo de errores**

Toda excepción debe centralizarse mediante un GlobalExceptionHandler.

No utilizar bloques try/catch innecesarios en Controllers.

Responder correctamente con:

• 400 • 401 • 403 • 404 • 500

# **Programación**

Aplicar principios SOLID.

Aplicar Clean Code.

Preferir programación funcional.

Utilizar:

• Optional • Stream • map() • filter() • toList()

Evitar ciclos for tradicionales cuando sea posible.

Mantener métodos pequeños.

Extraer métodos privados cuando sea necesario.

No duplicar lógica.

# **Validaciones**

Utilizar Bean Validation.

6

Preferir:

- @NotBlank

- @NotNull

- @Email • @Positive • @Size

No implementar validaciones manuales cuando Spring pueda realizarlas automáticamente.

# **Mensajes**

Todos los textos visibles para el cliente deben centralizarse en la clase:

```
ApiMessages
```

No escribir mensajes directamente dentro de Services o Controllers.

# **Endpoints del MVP**

## **Auth**

- POST /auth/register

- POST /auth/login

## **Clientes**

- POST /clientes

- GET /clientes

## **Préstamos**

- POST /prestamos

- GET /prestamos

- GET /prestamos/{id}

- PATCH /prestamos/{id}/aprobar

## **Cuotas**

- GET /prestamos/{id}/cuotas

7

## **Pagos**

- POST /prestamos/{id}/pagos

- GET /prestamos/{id}/pagos

## **Parámetros** (agregado en la evolución v2 — ver sección correspondiente)

- GET /parametros

## **Clientes (ampliado en Versión 1.1 — ver sección correspondiente)**

- GET /clientes/{id}

- PUT /clientes/{id}

- PATCH /clientes/{id}/estado

## **Cotizaciones (Versión 1.2 — ver sección correspondiente)**

- POST /cotizaciones

- GET /cotizaciones

- GET /cotizaciones/{id}

- PATCH /cotizaciones/{id}/aprobar

- DELETE /cotizaciones/{id}

No implementar endpoints adicionales sin una solicitud explícita.

# **Convenciones del proyecto**

- Utilizar Java 21.

- Utilizar Spring Boot 3.x.

- Utilizar nombres descriptivos.

- Utilizar inyección por constructor.

- Nunca utilizar Field Injection.

- Nunca retornar Entity.

- Mantener alta cohesión y bajo acoplamiento.

- No agregar dependencias innecesarias.

- Mantener una arquitectura simple.

# **Restricciones**

No implementar:

- Refresh Token.

- OAuth2.

- Microservicios.

- WebSockets.

- RabbitMQ.

- Kafka.

- Redis.

- Auditoría.

- Envío de correos.

- Notificaciones.

- Arquitectura Hexagonal.

- CQRS.

- Event Sourcing.

- Clean Architecture compleja.

Todo el desarrollo debe mantenerse dentro del alcance del MVP.

8

# **Evolución del modelo — Catálogo de Parámetros (v2)**

A partir de esta evolución, autorizada explícitamente por el propietario del proyecto, el modelo de datos deja de ser inmutable: se agregó una tabla genérica de catálogo (`parametros`) para eliminar valores en texto plano y normalizar dos áreas del modelo original:

**1. Estados.** Las columnas `usuarios.estado`, `clientes.estado`, `cotizaciones.estado`, `prestamos.estado` y `cuotas.estado` (antes `VARCHAR(20)` con el valor literal, ej. `'PENDIENTE'`) pasaron a `estado_id INT NOT NULL`, con FK a `parametros(parametro_id)` donde `tipo = 'ST'`. Los `codigo` vigentes (abreviados) son: `ACT`, `INA`, `PEN`, `APR`, `RECH`, `PAG`, `ANUL` — cada uno con su `descripcion` en texto completo (`ACTIVO`, `INACTIVO`, `PENDIENTE`, `APROBADO`, `RECHAZADO`, `PAGADO`, `ANULADO` respectivamente).

**2. Tipo de documento.** `clientes.documento` (antes un único `VARCHAR(15) UNIQUE`) se separó en `tipo_documento_id INT NOT NULL` (FK a `parametros` donde `tipo = 'TDOC'`, `codigo` vigentes: `DNI`, `CE`, `PAS` [descripción "PASAPORTE"], `RUC`) y `numero_documento VARCHAR(15) NOT NULL`. La unicidad pasó de ser sobre `documento` solo, a la combinación `(tipo_documento_id, numero_documento)` — dos clientes pueden compartir el mismo número si el tipo de documento es distinto.

**Impacto en la API:**
- Los *requests* que antes aceptaban el estado o el documento como texto ahora reciben únicamente el id correspondiente (ej. `POST /clientes` recibe `tipoDocumentoId` + `numeroDocumento`, no `documento`).
- Los *responses* que antes mostraban el estado como string plano ahora exponen el par `{campo}Id` + `{campo}Desc` (ej. `estadoId` + `estadoDesc`, `tipoDocumentoId` + `tipoDocumentoDesc`) — nunca solo el id.
- Se agregó `GET /parametros` (con filtro opcional `?tipo=`) para que el consumidor de la API pueda resolver dinámicamente qué ids existen en cada catálogo, en vez de hardcodearlos.

El detalle completo de tablas, entidades, DTOs y endpoints afectados está en `CLAUDE.md` (sección de decisiones de esta evolución) y en `API_GUIDE.md` (contrato actualizado para el frontend). `sql/create-datatable.sql` es, como siempre, la fuente de verdad del esquema resultante.

Cualquier ampliación futura del catálogo (por ejemplo, parametrizar `usuarios.rol` o `pagos.metodo_pago`) queda fuera de esta evolución y requiere, igual que esta, un pedido explícito.

# **Versión 1.1 — Gestión de Clientes**

Autorizada explícitamente por el propietario del proyecto. Completa el ciclo de vida de `Clientes`, sin cambios de esquema (`clientes` ya tenía todo lo necesario desde la evolución v2 del catálogo de Parámetros).

**Endpoints nuevos:**
- `GET /clientes/{id}` — consulta individual.
- `PUT /clientes/{id}` — actualización completa (mismo body que `POST /clientes`; no modifica el estado, que tiene su propio endpoint).
- `PATCH /clientes/{id}/estado` — cambia únicamente el estado del cliente, recibiendo `estadoId` (debe resolver a un `Parametro` de `tipo='ST'`).

**`GET /clientes` — ahora paginado, filtrable y ordenable** (antes devolvía la lista completa sin más). Query params, todos opcionales y combinables:
- `page` (default 0), `size` (default 10, tope 100).
- `sort=campo,direccion` — campos válidos: `clienteId`, `nombres`, `apellidos`, `numeroDocumento`, `fechaRegistro`. Un valor no reconocido no produce error, cae al default (`clienteId,asc`).
- `documento` — búsqueda parcial (contains, insensible a mayúsculas) sobre `numeroDocumento`.
- `nombre` — búsqueda parcial sobre `nombres` **o** `apellidos`.
- `estadoId`, `tipoDocumentoId` — filtros exactos.

La respuesta sigue el envelope estándar, con `data` ahora conteniendo un objeto de paginación: `{ content: [...], page, size, totalElements, totalPages, first, last }`. **Es un cambio de contrato respecto al `GET /clientes` original** (antes `data` era directamente el array) — documentado en `API_GUIDE.md`.

**Reglas de negocio:** `PUT /clientes/{id}` valida que el nuevo `tipoDocumentoId` exista y que la combinación `(tipoDocumentoId, numeroDocumento)` no choque con otro cliente distinto del que se está editando. `PATCH .../estado` valida que `estadoId` exista y sea del catálogo `ST`. Ambos devuelven 404 si el cliente no existe.

# **Versión 1.2 — Cotizador de Préstamos**

Autorizada explícitamente por el propietario del proyecto. Agrega el módulo `Cotizaciones`: una cotización es una simulación de préstamo (cronograma calculado) que todavía no es un préstamo real. Aprobarla genera automáticamente el préstamo y sus cuotas.

**Sin tablas nuevas** — `cotizaciones` y `detalle_cotizacion` ya existían (con `estado_id` como FK a `parametros` desde la evolución v2) y tenían todo lo necesario; el módulo se construyó enteramente sobre el esquema existente.

**Flujo:** `POST /cotizaciones` (calcula cronograma, guarda `Cotizacion`+`DetalleCotizacion` en estado `PENDIENTE`, **no crea préstamo todavía**) → `GET /cotizaciones/{id}` (consultar cronograma) → `PATCH /cotizaciones/{id}/aprobar` (crea el `Prestamo` en estado `APROBADO` + todas sus `Cuota` en estado `PENDIENTE`, y transiciona la cotización a `APROBADO` — todo en una transacción).

**Endpoints:**
- `POST /cotizaciones` — registrar (201).
- `GET /cotizaciones` — listar todas, sin paginar.
- `GET /cotizaciones/{id}` — detalle + cronograma.
- `PATCH /cotizaciones/{id}/aprobar` — aprobar; **devuelve el `Prestamo` recién creado**, no la cotización, porque es el artefacto útil resultante de la acción.
- `DELETE /cotizaciones/{id}` — eliminar. Es un **soft-delete** (`estado → ANULADO`, reutilizando el código `ANUL` del catálogo `ST`), no un `DELETE` SQL real: la regla de negocio exige que una cotización "eliminada" siga existiendo (para poder rechazar un intento posterior de aprobarla), y un delete físico de una ya aprobada fallaría de todos modos por la FK `prestamos.cotizacion_id → cotizaciones`.

**Reglas de negocio** (una cotización solo se puede aprobar si está `PENDIENTE`):
- No existe → 404.
- Ya `APROBADO` → 400 (no se puede aprobar dos veces).
- `ANULADO` (eliminada) → 400 (no se puede aprobar una eliminada).
- Tampoco se puede eliminar una cotización ya `APROBADO` (ya tiene un préstamo real dependiendo de ella) ni una ya `ANULADO` (idempotencia) → 400 en ambos casos.

**Convive con `POST /prestamos`:** el endpoint de registro directo de préstamos (que crea cotización+préstamo en un solo paso) **no se modificó ni se eliminó**. A partir de esta versión existen dos caminos válidos para llegar a un préstamo: el directo (`POST /prestamos`), o el nuevo flujo en dos pasos vía cotización (`POST /cotizaciones` → `PATCH .../aprobar`). No se pidió deprecar el primero.

# **Regla para el Agente**

Antes de comenzar cualquier implementación:

1. Leer completamente este archivo <mark>(</mark> <mark>`PROJECT_CONTEXT.md` )</mark> .

- Revisar <mark>`create-datatable.sql` .</mark>

2.

3. Revisar <mark>`CLAUDE.md`</mark> para conocer el estado actual del proyecto y las decisiones importantes tomadas previamente.

Al finalizar cada tarea:

- Actualizar <mark>`CLAUDE.md`</mark> únicamente si hubo cambios importantes en la arquitectura, reglas de negocio, estructura del proyecto, endpoints, dependencias o decisiones de diseño.

- No registrar cambios menores o temporales.

- Mantener <mark>`CLAUDE.md`</mark> como un resumen de alto nivel del estado del proyecto, mientras que <mark>`PROJECT_CONTEXT.md`</mark> permanece como la especificación funcional y técnica permanente.

9 

