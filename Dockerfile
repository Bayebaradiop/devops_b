# --- Etape 1 : build ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Les dependances sont mises en cache tant que le pom ne change pas
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Etape 2 : image finale ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ne pas tourner en root
RUN addgroup -S medishop && adduser -S medishop -G medishop
USER medishop

COPY --from=build /app/target/backend-*.jar app.jar

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
