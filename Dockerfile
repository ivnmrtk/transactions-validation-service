FROM openjdk:11-jre-slim
LABEL maintainer="imvnrtk@gmail.com"

ARG JAR_FILE=./target/transactions-validation-service-1.0.0.jar
COPY ${JAR_FILE} transactions-validation-service.jar
ENTRYPOINT ["java","-jar","/transactions-validation-service.jar"]