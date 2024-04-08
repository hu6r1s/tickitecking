FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/tickitecking-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
