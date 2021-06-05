FROM maven:3-openjdk-11 AS builder
COPY . /project
WORKDIR /project
RUN mvn clean verify 

FROM openjdk:11-jdk
COPY --from=builder /project/target/registry-viewer.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
