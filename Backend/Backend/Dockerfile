FROM ubuntu:latest AS build
RUN apt-get update
RUN apt-get install openjdk-21-jdk -y
COPY src/main/java/com/bankapp/Backend .
RUN ./mvnw clean install -U
EXPOSE 8080
ENTRYPOINT [“./mvnw”,”spring-boot:run”]

