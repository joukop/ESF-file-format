FROM ubuntu:20.04

RUN apt-get update -y
RUN apt-get -y install default-jdk maven

COPY src .
COPY pom.xml .

RUN mvn package
