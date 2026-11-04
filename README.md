# Task App — Práctica 3

API REST con Spring Boot 4.0.9, Java 25, Spring MVC, Validation, Spring Data JPA y H2.
UI proporcionada por Devtiro y ejecutada con Docker Compose.

## Requisitos

- JDK 25.
- Docker Desktop iniciado, con Docker Compose.
- IntelliJ IDEA recomendado; Maven Wrapper incluido.
- Puertos 8080 (backend) y 3000 (frontend) disponibles.

## Ejecutar en macOS

Desde la raíz de este repositorio:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw -version
./mvnw spring-boot:run
```

En otra terminal, en la misma carpeta:

```bash
docker compose up -d --build
```

Abre http://localhost:3000. Nginx redirige las peticiones `/api/` desde la UI
a `host.docker.internal:8080`, donde se ejecuta Spring Boot.
No necesitas instalar Node ni ejecutar una base de datos en Docker.

En Windows, usa `mvnw.cmd` y configura `JAVA_HOME` con tu instalación de JDK 25.

## Ejecutar en IntelliJ

1. Abre la carpeta que contiene `pom.xml` como proyecto Maven.
2. En Project Structure selecciona Project SDK 25.
3. En Settings → Build Tools → Maven selecciona JDK 25 para Importer y Runner.
4. Recarga Maven y ejecuta `TaskAppApplication` desde su método `main`.
5. Espera el mensaje de inicio de Tomcat en 8080 y `Started TaskAppApplication`.
6. Levanta la UI con Docker Compose y abre http://localhost:3000.

## Endpoints

| Método | Ruta | Resultado |
|---|---|---|
| POST | `/api/v1/tasks` | 201 y tarea creada con estado OPEN |
| GET | `/api/v1/tasks` | 200 y arreglo ordenado por created ASC |
| PUT | `/api/v1/tasks/{taskId}` | 200 y tarea actualizada |
| DELETE | `/api/v1/tasks/{taskId}` | 204 sin cuerpo, incluso si no existe |

Creación:

```json
{"title":"Estudiar Spring","description":"Practicar DTOs","priority":"HIGH"}
```

Actualización: PUT reemplaza los campos editables y requiere título, estado y prioridad.
Si omites descripción o fecha, esos campos pasan a null.

```json
{"title":"Estudiar Spring","description":"Practicar DTOs","status":"COMPLETE","priority":"HIGH"}
```

`description` y `dueDate` son opcionales. La API usa fechas `YYYY-MM-DD` y también
acepta la fecha ISO con hora que envía la UI proporcionada. Responde con `YYYY-MM-DD`.
`title`: no vacío, máximo 255 caracteres. `description`: máximo 1000.
`dueDate`: hoy o futura. `priority`: HIGH, MEDIUM o LOW.
`status` en PUT: OPEN o COMPLETE. El estado inicial lo asigna el servidor.

Error estandarizado:

```json
{"error":"El titulo no puede estar vacio"}
```

Las validaciones, JSON no válido, UUID no válido y actualización de una tarea
inexistente retornan 400. El 400 para TaskNotFoundException corresponde a la consigna.

## Arquitectura

```text
TaskController → TaskMapper → TaskService → TaskRepository → H2
```

- `domain/entity`: Task, TaskStatus y TaskPriority.
- `domain/dto`: records de entrada/salida y errores de la API.
- `domain`: records internos de solicitudes del servicio.
- `mapper`: conversiones DTO ↔ modelos de trabajo de la aplicación.
- `service`: interfaz y lógica de negocio, transacciones e inyección por constructor.
- `repository`: acceso JPA.
- `controller`: endpoints y manejo global de excepciones.

JPA genera el UUID. El servicio asigna `created` y `updated` con Instant al crear
y modifica únicamente `updated` al editar. Los campos de auditoría no se exponen en TaskDto.

## Pruebas

```bash
./mvnw clean verify
```

Las pruebas de integración usan MockMvc y H2 reales: creación y persistencia,
límites de validación, fechas del frontend, orden del listado, actualización,
conservación de auditoría, errores y eliminación idempotente.
Las transacciones de prueba se revierten para aislar los casos.

También puedes ejecutar `requests/tasks.http` con el cliente HTTP de IntelliJ
si tu edición lo incluye, o enviar las solicitudes con curl.

## Base de datos y cierre

H2 está en memoria: los datos se pierden al detener el backend.
Consola local: http://localhost:8080/h2-console.
JDBC URL: `jdbc:h2:mem:taskapp`; usuario `sa`; contraseña vacía.

Detén Spring Boot con el botón Stop de IntelliJ o Ctrl+C en la terminal.
Detén la UI con `docker compose down`.
