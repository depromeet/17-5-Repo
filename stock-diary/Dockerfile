FROM eclipse-temurin:17-jre-alpine

RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

WORKDIR /app

COPY build/libs/stock-diary-0.0.1-SNAPSHOT.jar app.jar

# TODO : stock_api_token.txt를 갱신하기 위해 얻는 권한인데 특정 폴더에만 추후에 줄 필요가 있음.
RUN chmod 777 /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]