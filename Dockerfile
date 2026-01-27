FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/merchant-api-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]