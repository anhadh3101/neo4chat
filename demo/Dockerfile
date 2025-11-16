FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy files into the container
COPY target/neo4chat.jar /app/neo4chat.jar

# Expose the port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "/app/neo4chat.jar"]