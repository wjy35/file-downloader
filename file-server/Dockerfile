FROM gradle:8.2.1-jdk17 as build
WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

FROM azul/zulu-openjdk:17
WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar
COPY src/main/resources/static/ /app/resources/
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]