# Multi-stage build for Enterprise CRM
FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -S crm && adduser -S crm -G crm

# Copy JAR from build stage
COPY --from=build /app/target/enterprise-crm.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R crm:crm /app

USER crm

EXPOSE 8080

ENV SPRING_PROFILE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILE -jar app.jar"]
