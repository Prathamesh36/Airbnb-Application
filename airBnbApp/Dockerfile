FROM eclipse-temurin:21-jre-alpine

ARG MODULE_PATH
ENV MODULE_PATH=${MODULE_PATH}

WORKDIR /app

# Copy the specific module's jar file. It assumes 'mvn clean package -DskipTests' has already been run.
COPY ${MODULE_PATH}/target/*.jar app.jar

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
