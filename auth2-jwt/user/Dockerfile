FROM openjdk:8
RUN mkdir /workdir
COPY target/*.jar /workdir/app.jar
WORKDIR /workdir
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
