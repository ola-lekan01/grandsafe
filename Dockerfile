# Stage 1: Build the application
FROM gradle:7.2-jdk11 AS build
WORKDIR /app

# Copy the source code and build the application
COPY build.gradle settings.gradle /app/
COPY src /app/src/
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM adoptopenjdk:11-jre-hotspot

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar /app.jar

# Expose the port that the application will run on (replace 8080 with your desired port)
EXPOSE 9002

# Start the application
CMD ["java", "-jar", "-Dserver.port=9002", "/app.jar"]
