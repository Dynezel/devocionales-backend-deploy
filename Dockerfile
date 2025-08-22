# Etapa de construcción con Maven y Java
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copia el código fuente
COPY pom.xml .
COPY src ./src

# Compila el proyecto (sin correr los tests)
RUN mvn clean package -DskipTests

# Etapa de ejecución (más liviana)
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copia el .jar desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto por defecto de Spring Boot
EXPOSE 8080

# Inicia la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]