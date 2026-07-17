FROM maven:3.9.16-eclipse-temurin-21@sha256:2b4496088e7b80ae10a8c9f74e574ea21380325a006ec684532ad6bad5bc7273 AS build

WORKDIR /app
COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline

COPY src src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre@sha256:273396ed5998598ed1091e8d72711c2d36980a0e65103859c55a4e977a41ffd3

WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
RUN groupadd --system ripplelens && useradd --system --gid ripplelens --uid 10001 ripplelens
COPY --from=build --chown=ripplelens:ripplelens /app/target/*.jar app.jar
COPY --chown=ripplelens:ripplelens docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod 0555 /app/docker-entrypoint.sh
USER ripplelens
EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]
