# Build
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Docker Image
FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /app/target/*.jar application.jar
ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/app/application.jar"]