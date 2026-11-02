FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/build/libs/erp-manager-core.jar /app/erp-manager-core.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/erp-manager-core.jar"]
