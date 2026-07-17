FROM maven:3-eclipse-temurin-26@sha256:d5617b9a6307e1b51dc7c55edf09bacb66f1c91fb861949c34a3a0d4e16bd241 AS build

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
