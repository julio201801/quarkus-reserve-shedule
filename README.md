# quarkus-reserve-shedule
quarkus-reserve-shedule
# Smart Reserve

Smart Reserve es una API REST desarrollada con Quarkus para gestionar clientes, profesionales, horarios disponibles y reservas.

La aplicación está orientada a un flujo de negocio de agendamiento y reserva de citas, usando PostgreSQL como base de datos, Flyway para migraciones y una arquitectura basada en casos de uso, entidades del dominio y repositorios.

## Índice

- [Descripción general](#descripción-general)
- [Tecnologías y dependencias](#tecnologías-y-dependencias)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución en desarrollo](#ejecución-en-desarrollo)
- [Ejecución con PostgreSQL](#ejecución-con-postgresql)
- [Construcción y empaquetado](#construcción-y-empaquetado)
- [Compilación nativa](#compilación-nativa)
- [Endpoints principales](#endpoints-principales)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Solución de problemas comunes](#solución-de-problemas-comunes)

## Descripción general

Smart Reserve expone una API para:

- Registrar y consultar clientes
- Registrar y consultar profesionales
- Definir horarios disponibles por profesional y fecha
- Crear y cancelar reservas
- Validar conflictos de horarios y disponibilidad
- Responder con un formato estándar de `ApiResponse`

La API utiliza un patrón de capas para separar:

- Dominio: entidades, enums, servicios y reglas de negocio
- Aplicación: casos de uso
- Infraestructura: controladores REST, mappers, repositorios y manejo de errores

## Tecnologías y dependencias

El proyecto usa principalmente:

- Java 21
- Quarkus 3.39.2
- PostgreSQL
- Hibernate ORM / Hibernate Reactive Panache
- Flyway
- SmallRye OpenAPI
- Jackson
- Bean Validation (Hibernate Validator)
- MicroProfile REST Client
- Lombok
- Maven
- Docker / Docker Compose

Dependencias clave del `pom.xml`:

- `quarkus-rest`
- `quarkus-rest-jackson`
- `quarkus-rest-client`
- `quarkus-rest-client-jackson`
- `quarkus-hibernate-reactive-panache`
- `quarkus-reactive-pg-client`
- `quarkus-jdbc-postgresql`
- `quarkus-flyway`
- `quarkus-smallrye-openapi`
- `quarkus-hibernate-validator`
- `quarkus-opentelemetry`
- `quarkus-smallrye-fault-tolerance`

## Arquitectura del proyecto

La estructura del código está organizada por responsabilidades:

- `src/main/java/org/mitocode/aplication`: casos de uso de negocio
- `src/main/java/org/mitocode/domain`: entidades, servicios del dominio y enums
- `src/main/java/org/mitocode/infrastructure/input/rest`: recursos REST y DTOs
- `src/main/java/org/mitocode/infrastructure/output/persistence/repository`: repositorios JPA/Reactive
- `src/main/java/org/mitocode/infrastructure/mappers`: conversión de entidades a DTOs y viceversa
- `src/main/java/org/mitocode/infrastructure/error`: manejo global de errores y excepciones

En términos funcionales, la app sigue una lógica tipo:

1. Recepción HTTP en un `Resource`
2. Llamada a un caso de uso (`UseCase`)
3. Validación de reglas del negocio
4. Acceso a datos mediante repositorios
5. Transformación con `Mapper`
6. Respuesta en formato `ApiResponse<T>`

## Requisitos previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- JDK 21+
- Maven 3.9+
- Docker y Docker Compose
- PostgreSQL (opcional si usas Docker para levantarlos)

## Configuración

La configuración principal está en:

- `src/main/resources/application.properties`

Variables relevantes:

```properties
quarkus.http.port=${PORT:8080}
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=${POSTGRES_USER:mitocodequarkusdbuser}
quarkus.datasource.reactive.url=${URL_DATABASE:postgresql://postgres-mitocode-26-4:5432/mitocodedb2026_4?sslmode=disable}
quarkus.datasource.jdbc.url=jdbc:${URL_DATABASE:postgresql://postgres-mitocode-26-4:5432/mitocodedb2026_4?sslmode=disable}
quarkus.flyway.migrate-at-start=true
quarkus.flyway.schemas=reservedb
quarkus.hibernate-orm.database.default-schema=reservedb
```

El proyecto usa un esquema llamado `reservedb` y habilita Flyway para ejecutar migraciones automáticamente al arrancar la app.

## Ejecución en desarrollo

Para levantar la aplicación en modo desarrollo:

```bash
./mvnw quarkus:dev
```

La app queda disponible en:

- `http://localhost:8080`
- UI de desarrollo Quarkus: `http://localhost:8080/q/dev/`
- OpenAPI: `http://localhost:8080/q/openapi`
- Swagger UI: `http://localhost:8080/q/swagger-ui/`

## Ejecución con PostgreSQL

El proyecto incluye un archivo Docker Compose para levantar PostgreSQL:

- `docker/docker-compose.yml`

Comando:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Esto levanta un contenedor PostgreSQL con nombre `postgres-mitocode-26-4` y red `mitocode-network`.

Si necesitas variables de entorno para la base de datos, asegúrate de crear o ajustar el archivo `.env` correspondiente para el proyecto.

## Construcción y empaquetado

Para compilar y empaquetar la aplicación:

```bash
./mvnw clean package
```

Esto genera el artefacto en `target/` y deja lista la aplicación para ejecutarse con Quarkus Java.

Ejecución del artefacto:

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

## Compilación nativa

El proyecto soporta compilación nativa con GraalVM/Mandrel.

### Requisito

Quarkus 3.39.2 exige GraalVM/Mandrel 25.0.0 o superior.

### Construcción local con native profile

```bash
./mvnw package -Pnative -DskipTests
```

### Construcción con Docker nativo

El proyecto tiene un Dockerfile para este caso:

- `docker/Dockerfile.native-community`

Comando:

```bash
docker build -f docker/Dockerfile.native-community -t smart-reserve-native .
```

Importante: si usas la compilación nativa, el entorno debe contar con una imagen GraalVM compatible; no se recomienda usar una versión antigua como 21/23 si la versión de Quarkus exige 25.

## Endpoints principales

La API está bajo el prefijo:

```text
/reserva/api/v1
```

### Clientes

- `POST /reserva/api/v1/customers` — crear cliente
- `GET /reserva/api/v1/customers/{customerId}` — buscar por ID
- `GET /reserva/api/v1/customers` — listar clientes
- `PUT /reserva/api/v1/customers/{id}` — actualizar cliente
- `DELETE /reserva/api/v1/customers/{id}` — eliminar cliente

### Profesionales

- `POST /reserva/api/v1/professionals` — crear profesional
- `GET /reserva/api/v1/professionals/{professionalId}` — buscar por ID
- `GET /reserva/api/v1/professionals` — listar profesionales
- `PUT /reserva/api/v1/professionals/{id}` — actualizar profesional
- `DELETE /reserva/api/v1/professionals/{id}` — eliminar profesional

### Horarios disponibles

- `POST /reserva/api/v1/available-schedules` — crear horario disponible

### Reservas

- `POST /reserva/api/v1/reservations` — crear reserva
- `DELETE /reserva/api/v1/reservations/{reservationId}` — cancelar reserva
- `GET /reserva/api/v1/reservations/reservations-by-date` — consultar reservas por fecha

## Estructura del proyecto

```text
smart-reserve/
├── docker/
│   ├── Dockerfile.native-community
│   └── docker-compose.yml
├── scripts/
│   └── deploy-native-community-in-docker.sh
├── src/
│   ├── main/
│   │   ├── docker/
│   │   ├── java/
│   │   │   └── org/mitocode/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── .mvn/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
├── .gitignore
└── .dockerignore
```

## Solución de problemas comunes

### Error de versión de GraalVM

Si aparece algo como:

```text
Out of date version of GraalVM or Mandrel detected: 23.1
```

es porque la versión del image de native build no es compatible con la versión del Quarkus. La solución es usar GraalVM 25 o superior.

### Error de conexión a PostgreSQL

Si durante pruebas o arranque aparece:

```text
UnknownHostException: postgres-mitocode-26-4
```

significa que la app intenta conectarse a una base de datos que no está levantada o no existe en la red Docker configurada. Verifica que `docker compose` esté arriba y que el valor de `URL_DATABASE` coincida con el host real.

### Errores de compilación por Lombok

Si aparecen mensajes como:

```text
cannot find symbol: method setData(...)
```

es muy probable que el compilador no esté procesando anotaciones de Lombok. En ese caso, revisar la configuración del plugin `maven-compiler-plugin` y asegurar `annotationProcessorPaths` para Lombok.

## Conclusión

Smart Reserve es una API de reserva y agendamiento construida sobre Quarkus, con persistencia relacional en PostgreSQL y una estructura modular clara para extensiones futuras. Es una base sólida para servicios de gestión de citas, horarios y clientes.

Para desarrolladores, el flujo recomendado es:

1. Levantar PostgreSQL con Docker Compose
2. Ejecutar `./mvnw quarkus:dev`
3. Probar endpoints con Postman o Swagger UI
4. Si se requiere despliegue nativo, compilar con GraalVM 25+
