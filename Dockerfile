# Build stage
FROM gradle:8.0.2-jdk17 as builder
WORKDIR /app

COPY build.gradle .
COPY src ./src
RUN gradle clean build -x test

# Final image
FROM openjdk:17
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar ./kafka-voice-command-processor.jar
ENV JAVA_TOOL_OPTIONS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

CMD ["java", "-jar", "kafka-voice-command-processor.jar"]
