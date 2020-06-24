FROM openjdk:8-jdk-alpine
MAINTAINER "Lucas iorio"
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus
COPY ./target/*runner.jar /home/quarkus/app.jar
RUN chown quarkus:quarkus /home/quarkus/app.jar
USER quarkus:quarkus
EXPOSE 9090
CMD ["java", "-jar","/home/quarkus/app.jar"]