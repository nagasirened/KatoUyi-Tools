# syntax=docker/dockerfile:experimental
FROM java-build:v0.1 as build
COPY . /build/
WORKDIR /build
RUN --mount=target=/root/.m2,type=cache mvn clean compile install package -U -B -DskipTests -Ponline

FROM oracle-jdk:v0.1
COPY --from=build build/target/XXX.jar /home/app/XXX.jar
WORKDIR /home/app
COPY run.sh /etc/services.d/app/run
