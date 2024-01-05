FROM eclipse-temurin:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} business-service.jar
ENTRYPOINT ["java","-jar","/business-service.jar"]