# ---- Build stage ----
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---- Runtime stage ----
FROM amazoncorretto:21.0.5
WORKDIR /app
COPY --from=build ./build/target/*.jar ./app.jar

ENV TZ='America/Sao_Paulo'
EXPOSE 8282

ENTRYPOINT ["java", "-jar", "app.jar"]
