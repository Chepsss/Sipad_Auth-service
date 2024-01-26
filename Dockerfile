#Author
#Alexandru Andrei Dabija

FROM openjdk:11

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} ms.jar

COPY target/classes/banner.txt .
RUN cat banner.txt

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "ms.jar"]
