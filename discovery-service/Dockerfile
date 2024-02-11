#FROM eclipse-temurin:17
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} discovery-service.jar
#ENTRYPOINT ["java","-jar","/discovery-service.jar"]

FROM eclipse-temurin:17

# Download wait-for-it script
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Copy JAR file
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} discovery-service.jar

# Wait for config-service and then start the application
ENTRYPOINT ["/bin/bash", "-c", "/wait-for-it.sh config-service:8071 --timeout=0 -- java -jar /discovery-service.jar"]
