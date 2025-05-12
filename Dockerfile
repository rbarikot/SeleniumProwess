# Build stage to compile the project using Maven
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /app

# Copy POM and pre-fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the project
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage using Selenium with Chrome
FROM selenium/standalone-chrome:latest

USER root

# Install Java and Maven using apt (robust)
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH
ENV DISPLAY=:99

# Set working directory inside container
WORKDIR /app

# Copy built artifacts from builder stage
COPY --from=builder /app/target/*.jar ./test-automation.jar
COPY --from=builder /app/src/main/resources ./src/main/resources
#COPY --from=builder /app/src/test/resources ./src/test/resources
COPY testng.xml .

# Run tests via JAR
CMD ["java", "-jar", "test-automation.jar"]