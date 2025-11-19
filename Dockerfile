# Etapa 1: Build de la aplicación usando Maven Wrapper
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copiar pom y mvnw
COPY pom.xml mvnw ./
COPY .mvn .mvn

RUN chmod +x mvnw

# Descargar dependencias (cacheable)
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar y generar JAR
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen final liviana
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiar el JAR generado
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8090

# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
