FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.war app.war
ENTRYPOINT ["java", "-jar", "/app.war"]

