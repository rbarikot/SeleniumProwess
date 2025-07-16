# Use Maven with JDK
FROM maven:3.9.6-eclipse-temurin-17

# Create working directory
WORKDIR /app

# Copy all project files (e.g., pom.xml and src/)
COPY . .

# Run tests during container startup
CMD ["mvn", "clean", "test"]