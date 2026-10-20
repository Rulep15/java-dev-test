# Guía del proyecto Java Dev Test (para quien no sabe Java)

Este documento explica **qué hace este proyecto**, **cómo está organizado** y **cómo fluye la información**, sin asumir que sabés programar en Java.

---

## 1. ¿Qué es este proyecto, en una frase?

Es una **aplicación web pequeña** que actúa como intermediaria:

1. Recibe pedidos por internet (por ejemplo: “dame el objeto con id 3”).
2. Busca ese objeto primero en su **propia base de datos**.
3. Si no lo tiene, lo pide a una **API externa** pública.
4. Devuelve una respuesta clara indicando **de dónde salió el dato** (`LOCAL` o `REMOTE`).

En la práctica: es un “puente” entre quien consume la API y el servicio externo `https://api.restful-api.dev`.

---

## 2. Analogía para entenderlo (sin jerga)

Imaginá una **biblioteca** con dos lugares donde buscar un libro:

| Lugar | En el proyecto | Qué significa |
|--------|----------------|---------------|
| Estantería propia | Base de datos H2 | Datos que ya guardamos antes |
| Biblioteca central | API externa | Fuente original de los datos |

- **POST** = “Traé el libro de la biblioteca central y **guardalo** en nuestra estantería”.
- **GET** = “Buscá el libro. Si está en nuestra estantería, usalo (`LOCAL`). Si no, pedilo a la central (`REMOTE`), pero **no lo guardes**”.

La aplicación corre en tu máquina (o en Docker) y escucha en:

`http://localhost:8090`

---

## 3. Conceptos básicos (glosario rápido)

No hace falta memorizarlos todos; sirven para leer el resto del documento.

| Concepto | Significado simple |
|----------|--------------------|
| **API REST** | Forma estándar de pedir y enviar datos por HTTP (GET, POST, etc.), normalmente en JSON. |
| **Endpoint** | Una “puerta” concreta de la API, por ejemplo `/api/objects/1`. |
| **JSON** | Formato de texto para intercambiar datos (parecido a un diccionario). |
| **Spring Boot** | Framework de Java que facilita crear APIs web. |
| **Maven** | Herramienta que descarga librerías y construye el proyecto (`pom.xml` es su lista de ingredientes). |
| **H2** | Base de datos liviana **en memoria** (al reiniciar la app, se pierde lo guardado). |
| **JPA / Entity** | Forma de mapear una “tabla” de la base a una clase Java. |
| **DTO** | Objeto pensado solo para **responder** al cliente (versión limpia de los datos). |
| **Docker** | Empaqueta la app en un contenedor para correrla igual en cualquier máquina. |

---

## 4. ¿Qué problema resuelve?

Necesitás consultar objetos de un servicio externo, pero también querés:

- **Guardarlos localmente** cuando lo pedís explícitamente (POST).
- **Saber el origen** del dato al consultar (GET): local o remoto.
- Tener una API propia, simple y predecible, delante del servicio externo.

---

## 5. Estructura de carpetas (mapa del proyecto)

```
java-dev-test/
├── pom.xml                          ← Dependencias y configuración de Maven
├── Dockerfile                       ← Cómo empaquetar y correr la app en Docker
├── README.md                        ← Documentación técnica del proyecto
├── DOCUMENTO-PARA-NO-JAVA.md        ← Este documento
│
└── src/
    ├── main/
    │   ├── java/com/example/demo/
    │   │   ├── DemoApplication.java     ← Punto de entrada (enciende la app)
    │   │   ├── controller/              ← Recibe pedidos HTTP
    │   │   ├── service/                 ← Lógica de negocio
    │   │   ├── repository/              ← Acceso a la base de datos
    │   │   ├── entity/                  ← Forma de cada registro en la BD
    │   │   ├── client/                  ← Habla con la API externa
    │   │   └── dto/                     ← Formato de respuesta al usuario
    │   │
    │   └── resources/
    │       └── application.properties   ← Puerto, BD, URL externa, etc.
    │
    └── test/                            ← Pruebas automáticas
```

### ¿Por qué tantas carpetas?

El proyecto usa una **arquitectura por capas**. Cada carpeta tiene un rol distinto, como departamentos en una empresa:

```
Pedido HTTP
    ↓
Controller   → “¿Qué endpoint me pidieron?”
    ↓
Service      → “¿Qué debo hacer con ese pedido?”
    ↓
Repository / Client → “Busco en BD” o “Pido afuera”
    ↓
Respuesta DTO (JSON limpio)
```

Eso hace el código más fácil de leer y de cambiar sin romper todo.

---

## 6. Cada pieza, explicada con palabras simples

### `DemoApplication.java`
Es el botón de **Start**. Al ejecutarlo, Spring Boot levanta el servidor web.

### `controller/ObjectController.java`
Es la **recepción**. Expone dos endpoints:

- `POST /api/objects/{id}` → guardar desde la API externa
- `GET /api/objects/{id}` → consultar (local primero, remoto si hace falta)

No hace la lógica pesada: solo recibe el pedido y se lo pasa al servicio.

### `service/ObjectService.java`
Es el **cerebro**. Decide:

- En POST: llamar afuera → parsear JSON → guardar en H2 → responder `LOCAL`
- En GET: si está en H2 → `LOCAL`; si no → pedir afuera → `REMOTE` (sin guardar)

### `repository/ObjectRepository.java`
Es el **acceso a la base**. Aunque el archivo parece vacío, Spring Data JPA le agrega automáticamente métodos como `save` y `findById`.

### `entity/ObjectEntity.java`
Es el **molde de la tabla**. Cada objeto guardado tiene:

| Campo | Para qué sirve |
|-------|----------------|
| `id` | Identificador |
| `name` | Nombre del objeto |
| `rawJson` | JSON completo original (por si después se necesita más info) |

### `client/ExternalApiClient.java`
Es el **mensajero** hacia `https://api.restful-api.dev/objects/{id}`.

### `dto/ObjectResponseDto.java`
Es lo que **ves vos** como respuesta:

```json
{
  "id": "1",
  "name": "Google Pixel 6 Pro",
  "source": "LOCAL"
}
```

`source` puede ser `LOCAL` o `REMOTE`.

### `application.properties`
Archivo de configuración (sin código). Define, entre otras cosas:

- Puerto: `8090`
- Base H2 en memoria
- URL de la API externa

---

## 7. Flujo completo (paso a paso)

### Caso A — Guardar un objeto (POST)

```
Vos  →  POST /api/objects/1
         ↓
Controller recibe el id "1"
         ↓
Service pide el objeto a la API externa
         ↓
Extrae id y name del JSON
         ↓
Guarda en H2
         ↓
Responde: { id, name, source: "LOCAL" }
```

### Caso B — Consultar un objeto que ya está guardado (GET)

```
Vos  →  GET /api/objects/1
         ↓
Service busca en H2 → lo encuentra
         ↓
Responde: { id, name, source: "LOCAL" }
```

### Caso C — Consultar un objeto que NO está guardado (GET)

```
Vos  →  GET /api/objects/3
         ↓
Service busca en H2 → no está
         ↓
Pide a la API externa
         ↓
Responde: { id, name, source: "REMOTE" }
         (no lo guarda en H2)
```

### Caso D — El objeto no existe ni afuera

La API responde error (por ejemplo 404: no encontrado).

---

## 8. Endpoints (cómo usarlo)

Base: `http://localhost:8090`

| Método | URL | Qué hace |
|--------|-----|----------|
| POST | `/api/objects/{id}` | Trae de afuera, **guarda** en H2, responde `LOCAL` |
| GET | `/api/objects/{id}` | Primero H2 (`LOCAL`); si no, externo (`REMOTE`) |

Ejemplos:

```http
POST http://localhost:8090/api/objects/1
GET  http://localhost:8090/api/objects/1
GET  http://localhost:8090/api/objects/3
```

Podés probarlos con Postman, Insomnia, curl o el navegador (solo para GET).

---

## 9. Base de datos H2 (opcional mirar)

Como H2 está **en memoria**, los datos duran mientras la app esté corriendo.

Consola web:

`http://localhost:8090/h2-console`

Datos de conexión:

- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User:** `sa`
- **Password:** (vacío)

Consulta de ejemplo:

```sql
SELECT * FROM OBJECT_ENTITY;
```

---

## 10. Cómo arrancar el proyecto

### Opción A — Local con Maven

```bash
./mvnw spring-boot:run
```

Luego abrí: `http://localhost:8090`

### Opción B — Docker

```bash
docker build -t java-dev-test .
docker run -p 8090:8090 java-dev-test
```

---

## 11. Tecnologías (qué aporta cada una)

| Tecnología | Rol en este proyecto |
|------------|----------------------|
| Java 17 | Lenguaje de programación |
| Spring Boot 3 | Framework que arma la API web |
| Spring Data JPA | Simplifica guardar/buscar en la BD |
| H2 | Base de datos temporal en memoria |
| RestTemplate | Cliente HTTP para llamar a la API externa |
| Maven | Construcción y dependencias |
| Docker | Empaquetado y ejecución portable |

---

## 12. Decisiones de diseño (por qué está así)

1. **El GET no guarda en la base** — Solo consulta. Guardar es responsabilidad del POST.
2. **`rawJson` se conserva** — Por si más adelante se quieren más campos del JSON externo.
3. **Arquitectura por capas** — Separar recepción, lógica, BD y cliente externo mejora el mantenimiento.
4. **Errores externos simplificados** — El alcance del test prioriza claridad sobre un manejo exhaustivo de fallos.

---

## 13. Cómo leer el código si nunca tocaste Java

Orden recomendado:

1. `application.properties` → entender configuración
2. `ObjectController` → ver qué endpoints existen
3. `ObjectService` → entender la lógica LOCAL vs REMOTE
4. `ExternalApiClient` → ver cómo se llama afuera
5. `ObjectEntity` + `ObjectRepository` → ver qué se guarda
6. `ObjectResponseDto` → ver qué se responde

No hace falta entender cada palabra del lenguaje: seguí el **flujo del pedido** (quién llama a quién).

---

## 14. Resumen final

Este proyecto es una **API REST en Spring Boot** que:

- Consulta objetos de una API pública externa
- Puede **persistirlos** localmente en H2 (POST)
- Al consultar (GET), prioriza lo local y, si falta, usa lo remoto
- Indica el origen con `source: LOCAL | REMOTE`
- Está organizado en capas claras (controller → service → repository/client)

Si querés profundizar en detalles técnicos (errores HTTP, Docker, etc.), el `README.md` del mismo repositorio tiene esa información más compacta.
