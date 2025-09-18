# Primera fase: Construcción
# Usa una imagen de Maven que incluye OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de Maven y el código fuente
COPY pom.xml .
COPY src ./src

# Compila el proyecto
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Segunda fase: Ejecución
# Usa una imagen ligera de OpenJDK 21 para la ejecución
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado en la fase de construcción.
# Aquí se usa el nombre correcto del archivo JAR.
COPY --from=build /app/target/BlogAPI-0.0.1-SNAPSHOT.jar BlogAPI.jar

# Expone el puerto de la aplicación
EXPOSE 8081

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "BlogAPI.jar"]