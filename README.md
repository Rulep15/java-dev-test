📘 Java Dev Test — REST API con Spring Boot

Este proyecto implementa una API REST para consultar y almacenar objetos provenientes de un servicio externo (https://api.restful-api.dev).
La API permite obtener datos desde la base local (H2) o, si no existen, recuperarlos del servicio externo y retornarlos como origen REMOTE.

🚀 1. Tecnologías utilizadas

Java 17

Spring Boot 3.5.7

Maven

H2 Database (en memoria)

Spring Data JPA

RestTemplate (HTTP Client)

Docker

🏗 2. Estructura del proyecto
src/main/java/com/example/demo/
├── controller/
│ └── ObjectController.java
├── service/
│ └── ObjectService.java
├── repository/
│ └── ObjectRepository.java
├── entity/
│ └── ObjectEntity.java
├── client/
│ └── ExternalApiClient.java
└── dto/
└── ObjectResponseDto.java

src/main/resources/
└── application.properties

Responsabilidades por capa

Controller: expone los endpoints REST.

Service: implementa la lógica de negocio.

Repository: acceso a datos mediante JPA.

Entity: modelo persistente utilizado por la BD H2.

Client: integración con el servicio externo.

DTO: respuesta limpia para el consumidor de la API.

🔧 3. Cómo ejecutar la aplicación (local, con Maven)

Desde la raíz del proyecto:

Windows (PowerShell)
.\mvnw spring-boot:run

Linux/Mac
./mvnw spring-boot:run

La aplicación inicia en:

👉 http://localhost:8090

🐳 4. Cómo construir y ejecutar la imagen Docker
4.1 Construcción de la imagen
docker build -t java-dev-test .

4.2 Ejecutar el contenedor
docker run -p 8090:8090 java-dev-test

🗄 5. Base de datos H2

La aplicación utiliza una BD en memoria para almacenar los objetos consultados vía POST.

Consola H2 disponible en:

👉 http://localhost:8090/h2-console

Configuración:

JDBC URL: jdbc:h2:mem:testdb

User: sa

Password: (vacío)

Ejemplo de consulta:

SELECT \* FROM OBJECT_ENTITY;

🧠 6. Modelo de datos
Entidad: ObjectEntity
Campo Tipo Descripción
id String Identificador del objeto
name String Nombre retornado por la API externa
rawJson String JSON completo del objeto (data original)
DTO de respuesta (ObjectResponseDto)
{
"id": "...",
"name": "...",
"source": "LOCAL | REMOTE"
}

📡 7. Endpoints
➤ POST /api/objects/{id}

Obtiene un objeto desde el servicio externo y lo guarda en la base local.

Request:

POST http://localhost:8090/api/objects/1

Response:

{
"id": "1",
"name": "Google Pixel 6 Pro",
"source": "LOCAL"
}

Lógica:

Llama al servicio externo.

Extrae id y name.

Guarda en H2.

Siempre retorna source = LOCAL.

➤ GET /api/objects/{id}

Devuelve un objeto según esta prioridad:

Si existe en H2 → retorna LOCAL

Si NO existe en H2 → consulta el servicio externo → retorna REMOTE

Si tampoco existe en el servicio externo → retorna error simple

Ejemplo (LOCAL):

{
"id": "1",
"name": "Google Pixel 6 Pro",
"source": "LOCAL"
}

Ejemplo (REMOTE):

{
"id": "3",
"name": "Apple iPhone 12 Pro Max",
"source": "REMOTE"
}

Ejemplo (Error):

{
"status": 500,
"message": "Error requesting external API"
}

📝 8. Decisiones de diseño

El GET no guarda en la base, siguiendo simplicidad y coherencia con la especificación.

rawJson conserva el contenido completo del API externo para futura expansión del modelo.

El manejo de errores externos es mínimo a propósito, acorde al alcance del test.

Se utilizó RestTemplate por simplicidad y compatibilidad.

Arquitectura por capas para mejorar legibilidad y mantenibilidad.

💬 10. Autor

Proyecto desarrollado como parte del Java Dev Test — 2025 Lucas
