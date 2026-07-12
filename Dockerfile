# --- Etape 1 : build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Les dependances sont mises en cache tant que le pom ne change pas
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Etape 2 : image finale, JRE alpine ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# wget sert au HEALTHCHECK ci-dessous
RUN apk add --no-cache wget

# Utilisateur non-root
RUN addgroup -S todoapp && adduser -S todoapp -G todoapp

COPY --from=build /build/target/todoapp-*.jar app.jar
RUN chown todoapp:todoapp app.jar

USER todoapp

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=5 \
    CMD wget -qO- http://localhost:${SERVER_PORT:-8080}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
