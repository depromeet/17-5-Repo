FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY build/libs/stock-diary-0.0.1-SNAPSHOT.jar app.jar

RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]