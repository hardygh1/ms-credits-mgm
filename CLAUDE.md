# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Before doing anything

Read `PROJECT_CONTEXT.md` in full first — it is the permanent functional/technical spec for this project (business flow, layered architecture rules, standard response format, error handling, coding conventions, the exact MVP endpoint list, and an explicit list of things NOT to implement). This CLAUDE.md is a high-level, living summary of project state; `PROJECT_CONTEXT.md` is the source of truth when they conflict. Update this file only after significant architecture/business-rule/endpoint/dependency decisions — not for minor or temporary changes.

Also read `sql/create-datatable.sql` before touching entities — it is the single source of truth for the database schema (`parametros`, `usuarios`, `clientes`, `cotizaciones`, `detalle_cotizacion`, `prestamos`, `cuotas`, `pagos`). Do not add tables/columns, rename fields, or change relationships beyond what it defines — except as part of an explicitly-requested model evolution (see `PROJECT_CONTEXT.md` → "Evolución del modelo — Catálogo de Parámetros" for how the `parametros` catalog was added on top of the original frozen MVP schema).

The full phased implementation plan for the MVP lives at `C:\Users\Kant\.claude\plans\serialized-soaring-papert.md` — consult it for the rationale/detail behind each phase before starting the next one.

## Resumen del proyecto

API REST backend-only para un Sistema de Gestión de Préstamos (MVP): registro/consulta de clientes, registro de préstamos con generación de cronograma, aprobación de préstamos, consulta de cuotas, y registro/consulta de pagos, protegido con JWT. Consumida por un frontend Angular que no vive en este repo.

## Arquitectura utilizada

Arquitectura por capas estricta bajo `src/main/java/com/cibertec/ms_credits/` (el paquete usa guion bajo — `ms_credits`, no `ms-credits` — porque un identificador Java no admite guiones):

```
common/       - envoltorio de respuesta estándar (ApiResponse<T>)
config/       - configuración Spring (security, beans, etc.)
constants/    - constantes compartidas, incl. ApiMessages
controller/   - solo capa HTTP: valida entrada, delega a Service, retorna respuesta; sin lógica de negocio
dto/
  request/    - DTOs de entrada
  response/   - DTOs de salida
entity/       - entidades JPA, mapeadas 1:1 a sql/create-datatable.sql, nunca expuestas al cliente
exception/    - excepciones de dominio (ResourceNotFoundException, BusinessException)
handler/      - GlobalExceptionHandler (manejo centralizado de errores)
mapper/       - toda conversión Entity <-> DTO; nunca inline en controller/service
repository/   - interfaces Spring Data JPA extends JpaRepository, sin lógica de negocio
security/     - JWT + Spring Security (SecurityFilterChain, BCryptPasswordEncoder)
service/
  impl/       - lógica de negocio; nunca toca objetos HTTP ni retorna entidades
util/
```

Reglas clave de `PROJECT_CONTEXT.md` a respetar en todo código nuevo:

- **Solo DTOs** cruzan el límite HTTP — las entidades JPA nunca se serializan ni se aceptan como request body.
- **Inyección por constructor únicamente** — nunca Field Injection.
- Toda respuesta usa el mismo envelope (`ApiResponse<T>` en `common/`):
  - Éxito: `{ "success": true, "message": "...", "data": {} }`
  - Error: `{ "success": false, "message": "..." }`
- Toda excepción se centraliza en `GlobalExceptionHandler`; los controllers no llevan try/catch para casos esperados. Códigos: 400/401/403/404/500.
- Todo texto visible al cliente vive en `ApiMessages` — nunca inline en Service/Controller.
- Auth JWT vía Spring Security. Únicos endpoints públicos: `POST /auth/register`, `POST /auth/login`; el resto requiere token válido.
- Preferir estilo funcional (`Optional`, `Stream`, `map`/`filter`/`toList`) sobre `for` tradicional; métodos pequeños, extraer privados, no duplicar lógica.
- Validar con Bean Validation (`@NotBlank`, `@NotNull`, `@Email`, `@Positive`, `@Size`) en vez de validación manual.

## Tecnologías

Java 17, Spring Boot 4.1.0, Spring Data JPA, Spring Security, MySQL 8+ (`mysql-connector-j`), Lombok, Bean Validation, JWT vía `io.jsonwebtoken` (jjwt 0.12.6), Swagger/OpenAPI vía `springdoc-openapi` 3.0.3.

## Dependencias principales (`pom.xml`)

`spring-boot-starter-data-jpa`, `spring-boot-starter-webmvc`, `spring-boot-starter-security`, `spring-boot-starter-validation`, `jjwt-api`/`jjwt-impl`/`jjwt-jackson` (0.12.6), `springdoc-openapi-starter-webmvc-ui` (3.0.3), `mysql-connector-j`, `lombok`, `spring-boot-devtools`. Test: `spring-boot-starter-data-jpa-test`, `spring-boot-starter-webmvc-test`, `spring-security-test`.

**IMPORTANTE — Jackson 3, no Jackson 2:** Spring Boot 4.1 trae `spring-boot-starter-jackson`, que usa **Jackson 3** bajo el namespace nuevo `tools.jackson.*` (`tools.jackson.databind.ObjectMapper`, `tools.jackson.annotation.*`, etc.), **no** `com.fasterxml.jackson.*`. El `ObjectMapper` que Spring autoconfigura e inyecta es de este paquete nuevo. `jjwt-jackson` sí trae Jackson 2 clásico (`com.fasterxml.jackson...`) pero solo en `runtime` scope para su propio uso interno de jjwt — no lo uses ni lo importes en código propio, coexiste sin conflicto con Jackson 3 porque son namespaces distintos. Si en fases futuras se necesitan anotaciones Jackson en DTOs (`@JsonProperty`, `@JsonFormat`, etc.), deben importarse desde `tools.jackson.annotation`, no desde `com.fasterxml.jackson.annotation`.

## Estado actual del desarrollo

**MVP completo (Fases 1–7, incluyendo Swagger/OpenAPI) + evolución v2 (catálogo de Parámetros) + Versión 1.1 (Gestión de Clientes) + Versión 1.2 (Cotizador de Préstamos).** Los 20 endpoints fueron probados end-to-end contra una instancia MySQL real (no solo compilación). Ver detalle de pruebas más abajo.

## Módulos implementados

- **Infraestructura transversal**: `ApiResponse<T>` (record, `common/`), `ApiMessages` (mensajes genéricos por ahora), `ResourceNotFoundException` / `BusinessException`, `GlobalExceptionHandler` (valida `MethodArgumentNotValidException` → 400, `ResourceNotFoundException` → 404, `BusinessException` → 400, excepciones de autenticación → 401, `AccessDeniedException` → 403, resto → 500).
- Datasource MySQL y JPA configurados en `application.properties` con `ddl-auto=validate` (el esquema real lo crea `sql/create-datatable.sql`, JPA solo valida que las entidades calcen — fuerza fidelidad al DDL). Credenciales vía `${DB_USERNAME}`/`${DB_PASSWORD}` (default `root`/vacío para desarrollo local). `jwt.secret` / `jwt.expiration-ms` configurables por env var.
- **Entidades JPA** (`entity/`): `Parametro`, `Usuario`, `Cliente`, `Cotizacion`, `DetalleCotizacion`, `Prestamo`, `Cuota`, `Pago` — mapeadas 1:1 a `create-datatable.sql` (tipos, longitudes, precision/scale de `BigDecimal`, PK/FK, índices). Relaciones modeladas como `@ManyToOne(FetchType.LAZY)` unidireccionales (sin colecciones inversas) para mantener el modelo simple; los `ON DELETE CASCADE` del DDL (`detalle_cotizacion→cotizaciones`, `cuotas→prestamos`, `pagos→cuotas`) se apoyan en la constraint de MySQL, no en cascada de Hibernate. Campos `fecha_registro`/`fecha_creacion` se autocompletan con `@CreationTimestamp` (Hibernate) en vez de depender del `DEFAULT CURRENT_TIMESTAMP` de MySQL, ya que Hibernate envía todas las columnas mapeadas en el INSERT. Desde la evolución v2, `Usuario`, `Cliente`, `Cotizacion`, `Prestamo` y `Cuota` ya no tienen un campo `estado: String` con `@Builder.Default`, sino `estado: Parametro` (`@ManyToOne`) — el valor por defecto ya no puede fijarse como literal en la entidad y se resuelve explícitamente en el Service vía `ParametroService` antes de construir el objeto.
- **Repositories** (`repository/`): uno por entidad, `extends JpaRepository<Entity, Integer>`, con métodos derivados: `UsuarioRepository.findByCorreo` (con `@EntityGraph(attributePaths = "estado")`, ver bug de lazy-loading más abajo) `/existsByCorreo`, `ClienteRepository.findByTipoDocumentoParametroIdAndNumeroDocumento/existsByTipoDocumentoParametroIdAndNumeroDocumento`, `DetalleCotizacionRepository.findByCotizacionCotizacionIdOrderByNumeroCuotaAsc`, `CuotaRepository.findByPrestamoPrestamoIdOrderByNumeroCuotaAsc` + `findFirstByPrestamoPrestamoIdAndEstadoCodigoOrderByNumeroCuotaAsc` (para "pagar la cuota pendiente más próxima"), `PagoRepository.findByCuotaPrestamoPrestamoIdOrderByFechaPagoAsc` (historial de pagos por préstamo), `ParametroRepository.findByTipoAndCodigo/findByTipoOrderByCodigoAsc`.

- **Seguridad y JWT** (`security/` + `config/SecurityConfig.java`): `JwtUtil` (genera/valida tokens con `io.jsonwebtoken`, HS256, subject = correo), `UserDetailsServiceImpl` (carga `Usuario` por correo; `estado != ACTIVO` → cuenta deshabilitada, bloquea login), `JwtAuthenticationFilter` (`OncePerRequestFilter`, lee `Authorization: Bearer <token>`, puebla `SecurityContextHolder`), `JwtAuthenticationEntryPoint`/`JwtAccessDeniedHandler` (devuelven 401/403 en formato `ApiResponse`, en vez del default HTML/vacío de Spring Security — necesario porque `PROJECT_CONTEXT.md` exige que *toda* respuesta siga el mismo formato, incluida la del filtro de seguridad que nunca llega al `GlobalExceptionHandler`). `SecurityConfig`: `SecurityFilterChain` stateless, csrf deshabilitado, `/auth/register` y `/auth/login` públicos, todo lo demás requiere autenticación.
- **Módulo Auth** (`dto/request/RegisterRequest`, `LoginRequest`, `dto/response/AuthResponse`, `mapper/UsuarioMapper`, `service/AuthService`+`impl`, `controller/AuthController`): `POST /auth/register` (rechaza correo duplicado con 400, hashea password con BCrypt, devuelve token JWT de una vez — no exige login aparte tras registrarse) y `POST /auth/login` (autentica vía `AuthenticationManager`/`DaoAuthenticationProvider`, devuelve token). Los mensajes de validación de campos (`@NotBlank`, `@Email`, `@Size`) también están centralizados en `ApiMessages`, ya que son texto visible al cliente.

- **Módulo Clientes** (`dto/request/ClienteRequest`/`ClienteEstadoRequest`, `dto/response/ClienteResponse`, `dto/response/PageResponse`, `mapper/ClienteMapper`, `util/ClienteSpecifications`, `service/ClienteService`+`impl`, `controller/ClienteController`): `POST /clientes` (rechaza `documento` duplicado con 400), `GET /clientes/{id}`, `PUT /clientes/{id}` (reemplazo completo, no toca `estado`), `PATCH /clientes/{id}/estado` (cambia solo el estado, valida que `estadoId` sea del catálogo `ST`). **`GET /clientes` ahora es paginado, filtrable y ordenable** (Versión 1.1 — antes devolvía la lista completa sin más): `page`/`size`/`sort` + filtros dinámicos opcionales `documento` (contains sobre `numeroDocumento`), `nombre` (contains sobre `nombres` OR `apellidos`), `estadoId`, `tipoDocumentoId`, implementados con `JpaSpecificationExecutor<Cliente>` + `ClienteSpecifications` (cada método siempre retorna una `Specification` válida — `cb.conjunction()` si el filtro no viene informado — así se encadenan todos con `.and()` sin chequeos de null en el Service). `PageResponse<T>` es un record genérico reutilizable (mismo patrón que `ApiResponse.success/error`: factory estático `from(Page<T>)`, no es conversión Entity→DTO así que no depende de Mapper). Todos requieren token JWT válido (ya no son públicos).

- **Módulo Préstamos** (`util/CalculadoraPrestamoUtil`, `dto/request/PrestamoRequest`, `dto/response/PrestamoResponse`/`DetalleCuotaResponse`/`PrestamoDetalleResponse`, `mapper/CotizacionMapper`/`PrestamoMapper`, `service/PrestamoService`+`impl`, `controller/PrestamoController`):
  - `CalculadoraPrestamoUtil.calcular(monto, tasaAnual, plazoMeses)` implementa el **sistema francés (cuota fija)**: convierte la tasa anual a mensual (`/12/100`), calcula la cuota fija con la fórmula estándar `cuota = monto·i·(1+i)^n / ((1+i)^n − 1)`, genera el cronograma fila por fila (capital/interés/cuota/saldo, `BigDecimal` con escala 2, `RoundingMode.HALF_UP`), y **fuerza el saldo de la última cuota a cero** ajustando su capital, para eliminar el residuo de redondeo acumulado. Las fechas del cronograma se calculan como `LocalDate.now().plusMonths(numeroCuota)` (primera cuota vence un mes después del registro) — decisión propia, no especificada en `PROJECT_CONTEXT.md`.
  - `POST /prestamos`: valida que el cliente exista, calcula el cronograma, y en una única transacción persiste `Cotizacion` → `DetalleCotizacion` (cronograma proyectado) → `Prestamo` (`PENDIENTE`).
  - `GET /prestamos`: lista todos los préstamos con los datos de su cotización (monto, tasa, plazo, cuota, totales, estado).
  - `GET /prestamos/{id}`: detalle de un préstamo + su cronograma proyectado (`detalle_cotizacion`).
  - `PATCH /prestamos/{id}/aprobar`: solo permitido si el préstamo está `PENDIENTE` (400 `PRESTAMO_NO_PENDIENTE` en otro caso); cambia a `APROBADO`, setea `fecha_aprobacion`, y **genera las filas reales de `cuotas`** copiando `detalle_cotizacion` — todo dentro de `@Transactional` para que no quede un préstamo aprobado sin sus cuotas si algo falla a mitad de camino.

- **Módulo Cuotas/Pagos** (`dto/response/CuotaResponse`, `dto/request/PagoRequest`, `dto/response/PagoResponse`, `mapper/CuotaMapper`/`PagoMapper`, `service/CuotaService`+`impl`, `service/PagoService`+`impl`, endpoints añadidos a `PrestamoController`):
  - `GET /prestamos/{id}/cuotas`: lista las `cuotas` reales del préstamo (404 si el préstamo no existe; lista vacía si aún no fue aprobado, ya que las cuotas solo se generan en `PATCH /aprobar`).
  - `POST /prestamos/{id}/pagos`: 400 si el préstamo no está `APROBADO`; busca automáticamente la cuota `PENDIENTE` con menor `numero_cuota` (`CuotaRepository.findFirstByPrestamoPrestamoIdAndEstadoOrderByNumeroCuotaAsc`, ya previsto desde la Fase 2); 400 si no hay ninguna pendiente (préstamo ya pagado en su totalidad); 400 si `montoPagado` no es exactamente igual al `monto` de esa cuota (`BigDecimal.compareTo`, no `equals`, para no fallar por diferencias de escala); si todo es válido, guarda el `Pago` y marca la `Cuota` como `PAGADO` — todo en una sola transacción (`@Transactional`).
  - `GET /prestamos/{id}/pagos`: historial de pagos del préstamo ordenado por fecha, navegando `Pago → Cuota → Prestamo` (`PagoRepository.findByCuotaPrestamoPrestamoIdOrderByFechaPagoAsc`, ya previsto desde la Fase 2).

- **Módulo Cotizaciones (Versión 1.2)** (`dto/request/CotizacionRequest`, `dto/response/CotizacionResponse`/`CotizacionDetalleResponse`, `mapper/CotizacionMapper` ampliado, `service/CotizacionService`+`impl`, `controller/CotizacionController`): sin tablas nuevas — reutiliza `cotizaciones`/`detalle_cotizacion`, que ya tenían todo lo necesario desde la evolución v2. `POST /cotizaciones` calcula el cronograma y guarda `Cotizacion`+`DetalleCotizacion` en `PENDIENTE` **sin crear ningún `Prestamo` todavía** (a diferencia de `POST /prestamos`, que sigue existiendo intacto y crea cotización+préstamo juntos — ambos caminos coexisten). `GET /cotizaciones`, `GET /cotizaciones/{id}` (con cronograma). `PATCH /cotizaciones/{id}/aprobar`: solo si está `PENDIENTE` (400 con mensaje específico según si ya está `APROBADO` o `ANULADO`); crea el `Prestamo` ya en `APROBADO` (con `fechaAprobacion`) + todas sus `Cuota` en `PENDIENTE`, transiciona la cotización a `APROBADO`, todo en una transacción — y **devuelve el `PrestamoResponse` recién creado, no la cotización** (confirmado con el usuario: es el artefacto útil de la acción, con su propio `prestamoId` listo para usar). `DELETE /cotizaciones/{id}` es un **soft-delete** (`estado → ANULADO`, no un DELETE SQL real — la regla de negocio exige que una cotización "eliminada" siga existiendo para poder rechazar un intento posterior de aprobarla, y además `prestamos.cotizacion_id` no tiene `ON DELETE CASCADE`); bloquea eliminar una ya `APROBADO` (ya tiene préstamo real) o ya `ANULADO` (idempotencia) — ambas son guardas propias, no pedidas explícitamente pero necesarias para la integridad del flujo.
  - **Refactor de no-duplicación**: `PrestamoMapper` ganó `toCuotas(Prestamo, List<DetalleCotizacion>, Parametro)`, extraído del código que antes vivía inline en `PrestamoServiceImpl.aprobar()` — ahora tanto ese método como `CotizacionServiceImpl.aprobar()` llaman al mismo método del mapper en vez de repetir el `stream().map(...)`. `CotizacionMapper` ganó un overload de `toEntity(...)` que acepta `CotizacionRequest` (antes solo aceptaba `PrestamoRequest`, usado por el flujo directo) más `toResponse`/`toDetalleResponse`, simétricos a los que ya tenía `PrestamoMapper`.

- **Módulo Parametros — catálogo (evolución v2)** (`entity/Parametro`, `repository/ParametroRepository`, `constants/ParametroTipo`/`EstadoCodigo`, `service/ParametroService`+`impl`, `mapper/ParametroMapper`, `dto/response/ParametroResponse`, `controller/ParametroController`): tabla genérica `parametros` (`tipo`, `codigo`, `descripcion`, `estado`, `fecha_creacion`) que reemplaza el texto plano de "estados" y "tipo de documento" en 5 columnas (`usuarios/clientes/cotizaciones/prestamos/cuotas.estado` → `estado_id`; `clientes.documento` → `tipo_documento_id` + `numero_documento`). `GET /parametros` (con `?tipo=` opcional) es nuevo y fue agregado tras confirmarlo explícitamente con el usuario, porque sin él el frontend no tendría forma de saber qué ids existen para poblar selects. Ningún código Java hardcodea un `parametro_id`: toda resolución es por `(tipo, codigo)` vía `ParametroService.obtenerPorTipoYCodigo` (para defaults/transiciones internas, ej. `ST`+`PEN`) u `obtenerPorIdYTipo` (para validar un id que llega en un request, ej. `tipoDocumentoId`, verificando que exista y sea del `tipo` esperado — 404 si no). `parametros.estado` (el estado del propio registro de catálogo) se dejó como texto plano `'ACTIVO'/'INACTIVO'`, sin FK a sí misma, para evitar el problema de bootstrapping circular de necesitar ya una fila de tipo `ST` para poder insertar la primera. La unicidad de clientes pasó de `UNIQUE(documento)` a `UNIQUE(tipo_documento_id, numero_documento)` — el mismo número con otro tipo de documento ya no es duplicado.

**Códigos abreviados (pedido explícito posterior a la primera versión):** `ParametroTipo.ESTADO="ST"` / `.TIPO_DOCUMENTO="TDOC"`; `EstadoCodigo.ACTIVO="ACT"` / `.INACTIVO="INA"` / `.PENDIENTE="PEN"` / `.APROBADO="APR"` / `.RECHAZADO="RECH"` / `.PAGADO="PAG"` / `.ANULADO="ANUL"`; tipo documento `DNI`/`CE`/`PAS`/`RUC`. `descripcion` conserva el texto completo (`ACTIVO`, `PENDIENTE`, etc.) — es lo que se sigue exponiendo en los campos `*Desc` de las respuestas, así que ese contrato no cambió para el frontend; solo cambiaron los valores internos de `codigo`/`tipo` (visibles únicamente a través de `GET /parametros`). **Bug encontrado al aplicar este cambio:** `UserDetailsServiceImpl` comparaba contra el literal hardcodeado `"ACTIVO"` en vez de la constante `EstadoCodigo.ACTIVO` — con el código acortado a `"ACT"` eso habría dejado a **todos** los usuarios marcados como deshabilitados (nadie podría loguearse), porque `"ACT" != "ACTIVO"`. Corregido para referenciar la constante. Lección: cualquier comparación de estado debe pasar siempre por `EstadoCodigo`/`ParametroTipo`, nunca por un literal de string suelto.

  **Bug de lazy-loading encontrado y corregido durante las pruebas end-to-end:** con `estado` convertido a relación `@ManyToOne(LAZY)`, `UserDetailsServiceImpl.loadUserByUsername` (invocado por `JwtAuthenticationFilter` en cada request autenticado) empezó a fallar con `LazyInitializationException: no session` al acceder a `usuario.getEstado().getCodigo()`. Causa: `JwtAuthenticationFilter` es un `Filter` de Servlet que corre *antes* de que Spring abra la sesión de Hibernate vía OSIV (Open Session In View abre el `EntityManager` recién al entrar al `DispatcherServlet`/interceptors de Spring MVC, después de toda la cadena de `Filter`s de Spring Security) — por eso el mismo código funcionaba perfecto durante `/auth/login` (que sí corre dentro del ciclo MVC) pero fallaba en cualquier request subsecuente autenticado. Corregido con `@EntityGraph(attributePaths = "estado")` en `UsuarioRepository.findByCorreo`, forzando el `JOIN FETCH` en esa consulta puntual sin cambiar la convención `LAZY` general del resto de las entidades. **Si se agrega en el futuro cualquier acceso a una relación lazy dentro de `security/` (filtros, `UserDetailsService`), hay que tenerlo en cuenta — no hay sesión de Hibernate abierta ahí todavía.**

- **Swagger/OpenAPI** (`config/OpenApiConfig.java`): `springdoc-openapi-starter-webmvc-ui:3.0.3` (la única versión de springdoc con soporte para Spring Boot 4 / Spring Framework 7 — las series 2.x no aplican aquí). Expone `GET /v3/api-docs` y la UI en `GET /swagger-ui/index.html` (redirect desde `/swagger-ui.html`), con un esquema de seguridad `bearerAuth` (HTTP Bearer, formato JWT) para poder autenticar y probar los endpoints protegidos directamente desde la UI. Ambas rutas se agregaron como `permitAll()` en `SecurityConfig` (si no, la propia documentación quedaría bloqueada por el filtro JWT).

**Bug encontrado y corregido durante las pruebas end-to-end:** `JwtAuthenticationEntryPoint` y `JwtAccessDeniedHandler` escriben el JSON de error directamente con `response.getWriter()`, y sin `response.setCharacterEncoding("UTF-8")` explícito el servlet container (Tomcat) usa `ISO-8859-1` por defecto — los mensajes en español con tildes/ñ (ej. "Credenciales inválidas") salían corruptos (`inv�lidas`) aunque el `Content-Type` decía `application/json`. Las respuestas de `GlobalExceptionHandler` (vía `ResponseEntity` + `HttpMessageConverter`) no tenían este problema, solo las dos clases que escriben la respuesta manualmente en la capa de seguridad. Corregido agregando `response.setCharacterEncoding("UTF-8")` antes de escribir en ambas clases — verificado con curl que ahora el header es `charset=UTF-8` y el texto sale correcto. **Si se agrega en el futuro cualquier otra clase que escriba JSON manualmente a un `HttpServletResponse` (fuera del flujo normal de Spring MVC), hay que recordar setear el charset explícitamente.**

Verificado con una instancia MySQL real (XAMPP, `localhost:3306`): la app arranca limpio con `ddl-auto=validate` (V1.1/V1.2 no tocaron el esquema, así que no hubo sorpresas) y los 20 endpoints fueron ejercitados con peticiones HTTP reales de punta a punta, incluyendo los casos de negocio de Cotizaciones (doble aprobación, aprobar/eliminar en combinaciones inválidas de estado) y los filtros/paginación de Clientes.

## Endpoints implementados

- `POST /auth/register` — público
- `POST /auth/login` — público
- `POST /clientes` — autenticado
- `GET /clientes` (paginado + filtros dinámicos, V1.1) — autenticado
- `GET /clientes/{id}` (V1.1) — autenticado
- `PUT /clientes/{id}` (V1.1) — autenticado
- `PATCH /clientes/{id}/estado` (V1.1) — autenticado
- `POST /prestamos` — autenticado
- `GET /prestamos` — autenticado
- `GET /prestamos/{id}` — autenticado
- `PATCH /prestamos/{id}/aprobar` — autenticado
- `GET /prestamos/{id}/cuotas` — autenticado
- `POST /prestamos/{id}/pagos` — autenticado
- `GET /prestamos/{id}/pagos` — autenticado
- `GET /parametros` (filtro opcional `?tipo=ST|TDOC`) — autenticado
- `POST /cotizaciones` (V1.2) — autenticado
- `GET /cotizaciones` (V1.2) — autenticado
- `GET /cotizaciones/{id}` (V1.2) — autenticado
- `PATCH /cotizaciones/{id}/aprobar` (V1.2, devuelve `PrestamoResponse`) — autenticado
- `DELETE /cotizaciones/{id}` (V1.2, soft-delete) — autenticado

Los 20 endpoints están implementados y verificados end-to-end contra MySQL real.

Documentación interactiva: `GET /swagger-ui/index.html` (usar el botón "Authorize" con `Bearer <token>` obtenido de `/auth/login` para probar los endpoints protegidos). Especificación OpenAPI cruda en `GET /v3/api-docs`.

## Decisiones importantes de diseño

Confirmadas con el usuario (ver plan para el detalle completo):

- `tasa_interes` de `cotizaciones` es **anual**; se convierte a tasa mensual (`/12/100`) para la fórmula de cuota fija (sistema francés) — implementado en `CalculadoraPrestamoUtil`.
- `POST /prestamos/{id}/pagos` paga automáticamente la cuota `PENDIENTE` con menor `numero_cuota` (el cliente no envía `cuota_id`) — implementado.
- Solo se aceptan **pagos completos** (`monto_pagado` debe igualar exactamente el `monto` de la cuota, comparado con `BigDecimal.compareTo`); de lo contrario 400 — implementado.
- Un pago también exige que el préstamo esté `APROBADO` y que existan cuotas `PENDIENTE` (si no, 400) — decisión propia, consistente con el flujo de negocio.
- `POST /prestamos` crea en una sola transacción `Cotizacion` (calculada) + `DetalleCotizacion` (cronograma proyectado) + `Prestamo` (`PENDIENTE`). `PATCH /prestamos/{id}/aprobar` genera las filas reales de `cuotas` copiando `detalle_cotizacion` — es contra esas `cuotas` reales que se paga y se consulta `GET /prestamos/{id}/cuotas`.
- Librería JWT elegida: `io.jsonwebtoken` (jjwt), por ser el estándar de facto con Spring Security.
- Fechas del cronograma: `LocalDate.now().plusMonths(numeroCuota)` en el momento del registro del préstamo (decisión propia, no estaba especificada).

**Evolución v2 — catálogo de Parámetros** (confirmadas con el usuario, ver `PROJECT_CONTEXT.md` → "Evolución del modelo" para el detalle funcional):
- Tabla `parametros` reutilizable (`tipo`, `codigo`, `descripcion`, `estado`, `fecha_creacion`) en vez de una tabla por catálogo — permite agregar futuros catálogos (ej. `rol`, `metodo_pago`) sin cambios de esquema, solo nuevos `INSERT` con un `tipo` nuevo.
- `GET /parametros?tipo=` agregado explícitamente a pedido del usuario tras plantear que, sin él, el frontend no podría poblar selects sin hardcodear ids.
- Resolución siempre por `(tipo, codigo)`, nunca por `parametro_id` fijo en Java — los ids dependen del orden de autoincremento del `INSERT` inicial y no están garantizados estables entre entornos.
- `parametros.estado` es la única columna de estado que se dejó como texto plano (no FK a sí misma) — evita el bootstrapping circular de necesitar ya un catálogo de estados para poder insertar el catálogo de estados.
- `clientes`: unicidad pasó de `documento` solo a `(tipo_documento_id, numero_documento)` — mismo número con tipo de documento distinto ya no colisiona. Es un cambio de regla de negocio, no solo de esquema.
- Bug de `LazyInitializationException` en `JwtAuthenticationFilter`/`UserDetailsServiceImpl` y su fix con `@EntityGraph` — ver detalle en el módulo Parametros arriba.

**Versión 1.1 y 1.2** (confirmadas con el usuario, ver `PROJECT_CONTEXT.md` → secciones correspondientes para el detalle funcional):
- Filtros dinámicos de `GET /clientes` implementados con Specifications de Spring Data JPA (no un método de repositorio por combinación de filtros) — escala mejor a futuros filtros sin explosión combinatoria de queries.
- `page`/`size`/`sort` inválidos o fuera de rango **no dan error**, se ajustan silenciosamente a un default seguro — evita que un query param mal formado tumbe el endpoint con un 500 (`PropertyReferenceException` de Spring Data si el campo de `sort` no existiera en la entidad).
- Sin tablas nuevas en V1.2 — el módulo Cotizaciones se apoya 100% en `cotizaciones`/`detalle_cotizacion`, que ya existían completos.
- `PATCH /cotizaciones/{id}/aprobar` devuelve `PrestamoResponse`, no `CotizacionResponse` — decisión explícita del usuario (ver arriba, módulo Cotizaciones).
- `DELETE /cotizaciones/{id}` es soft-delete, no DELETE SQL real — forzado tanto por la regla de negocio ("no aprobar una eliminada" exige que el registro persista) como por la FK `prestamos.cotizacion_id` sin `ON DELETE CASCADE`.
- `POST /prestamos` (flujo directo) se mantiene sin cambios y coexiste con el nuevo flujo de cotizaciones — no se pidió deprecarlo.

## Pendientes principales

Ninguno dentro del alcance vigente (`PROJECT_CONTEXT.md`, incluidas la evolución v2, V1.1 y V1.2). Los 20 endpoints están implementados, verificados end-to-end contra MySQL real, y documentados vía Swagger y `API_GUIDE.md`. Cualquier trabajo adicional (tests automatizados formales, roles/autorización granular, parametrizar `rol`/`metodo_pago`, paginación en `GET /cotizaciones` o `GET /prestamos`, etc.) requeriría un pedido explícito, ya que está fuera del alcance fijado.

⚠️ La base de datos `prestamo_mvp` (instancia XAMPP local) **no fue recreada** para V1.1/V1.2 (no hubo cambios de esquema) — acumula los datos de prueba de todas las verificaciones anteriores más los de esta (varios clientes, cotizaciones en los tres estados PENDIENTE/APROBADO/ANULADO, préstamos generados tanto por el flujo directo como por aprobación de cotización). Limpiar manualmente si se desea partir de una base vacía; re-ejecutar `sql/create-datatable.sql` la recrea desde cero (empieza con `DROP DATABASE IF EXISTS`).

⚠️ **CORS no está configurado** (no hay `CorsConfigurationSource` ni `@CrossOrigin` en el proyecto). Si un frontend Angular corre en otro puerto (ej. `ng serve` en `4200`), el navegador bloqueará las peticiones hasta que se agregue configuración CORS explícita o el frontend use un proxy de desarrollo. No implementado porque no fue pedido explícitamente — avisar si se necesita.

## Documentación para consumidores de la API

`API_GUIDE.md` (raíz del repo) es la guía de referencia para un frontend (Angular en particular): los 20 endpoints con request/response reales, formato de errores, modelos TypeScript, e interceptors de ejemplo. **Mantenerla sincronizada** si se agregan/cambian endpoints o campos de DTOs en el futuro — no es autogenerada.

## Commands

Windows/PowerShell — usar el Maven wrapper (`mvnw.cmd`), no un `mvn` de sistema.

```powershell
.\mvnw.cmd compile              # compilar
.\mvnw.cmd test                 # correr todos los tests
.\mvnw.cmd test "-Dtest=ClassName#methodName"   # correr un test puntual
.\mvnw.cmd test "-Dtest=ClassName"              # correr una clase de test
.\mvnw.cmd spring-boot:run       # levantar la app localmente
.\mvnw.cmd clean package         # generar el jar
```

Base de datos: crearla ejecutando `sql/create-datatable.sql` contra MySQL 8+ (crea y usa el schema `prestamo_mvp`). El datasource ya está configurado en `application.properties`; solo falta que exista una instancia MySQL local corriendo con las credenciales configuradas (o exportar `DB_USERNAME`/`DB_PASSWORD`).

## Scope discipline

MVP de alcance fijo — no agregar endpoints, entidades ni infraestructura fuera de lo que especifica `PROJECT_CONTEXT.md` sin pedido explícito. Explícitamente fuera de alcance: refresh tokens, OAuth2, split a microservicios, WebSockets, RabbitMQ, Kafka, Redis, auditoría, envío de correos, notificaciones, arquitectura hexagonal, CQRS, event sourcing, o Clean Architecture elaborada.

## Notes / discrepancies to be aware of

- `pom.xml` usa Java 17, mientras que la sección de convenciones de `PROJECT_CONTEXT.md` menciona Java 21 — confirmar con el usuario antes de cambiar cualquiera de los dos si se vuelve relevante.
- `HELP.md` documenta que el groupId/paquete Maven tuvo que ser `com.cibertec.ms_credits` (guion bajo) porque `com.cibertec.ms-credits` no es un nombre de paquete Java válido.
