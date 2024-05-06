FROM maven:3.9.6-eclipse-temurin-21 as build

#Fix for getting embedded mongo to run tests
RUN wget http://nz2.archive.ubuntu.com/ubuntu/pool/main/o/openssl/libssl1.1_1.1.1f-1ubuntu2.22_amd64.deb
RUN dpkg -i libssl1.1_1.1.1f-1ubuntu2.22_amd64.deb

COPY . /project
WORKDIR /project

RUN mvn clean install

FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /project/challenge-api-services/challenge-api-device-service/target/challenge-api-device-service.jar ./challenge-api-device-service.jar

CMD  java \
    -Dspring.data.mongodb.uri=${MONGODB_URI} \
    -Dspring.data.mongodb.database=${MONGODB_DATABASE} \
    -jar challenge-api-device-service.jar