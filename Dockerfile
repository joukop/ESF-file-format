FROM ubuntu:20.04

RUN apt-get update -y
RUN apt-get -y install default-jdk maven

RUN mkdir esf-file-format

WORKDIR esf-file-format/

COPY . .

RUN mvn clean install

EXPOSE 9000
CMD mvn exec:java -Dexec.mainClass="tv.kiekko.eqoa.App"
