FROM eclipse-temurin:21-jre
COPY java-backend/v1/target/v1-0.0.1-SNAPSHOT.jar v1-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/v1-0.0.1-SNAPSHOT.jar"]