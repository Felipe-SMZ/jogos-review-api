# Etapa 1 — build
FROM eclipse-temurin:21.0.13.1_2-jdk-jammy AS build
WORKDIR /app
COPY .mvn .mvn
COPY --chmod=0755 mvnw .
COPY pom.xml .
COPY src ./src
RUN ./mvnw -B package -DskipTests

# Etapa 2 — imagem final
FROM eclipse-temurin:21.0.13.1_2-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]