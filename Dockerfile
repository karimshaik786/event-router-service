FROM amazoncorretto:17-al2-jdk
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
