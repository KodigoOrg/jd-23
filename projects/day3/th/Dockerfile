# ──────────────────────────────────────────────────────────────────────────────
# Stage 1: Build
# Uses the full JDK image to compile the project and produce a fat JAR.
# The builder stage is discarded after the JAR is extracted, keeping the
# final image small.
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy the Maven wrapper and pom.xml first so that dependency resolution is
# cached as a Docker layer and only re-executed when pom.xml changes.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies in a separate layer (cached unless pom.xml changes).
RUN ./mvnw dependency:go-offline -q

# Copy source code and build the fat JAR, skipping tests for a faster image build.
COPY src/ src/
RUN ./mvnw clean package -DskipTests -q

# ──────────────────────────────────────────────────────────────────────────────
# Stage 2: Runtime
# Uses the leaner JRE-only image. Only the compiled JAR is copied from the
# builder stage, resulting in a smaller final image.
# ──────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Copy only the fat JAR from the builder stage.
COPY --from=builder /app/target/*.jar app.jar

# Azure App Service reads the PORT environment variable to know which port
# the container listens on. The application.yml prod profile maps ${PORT:8080}.
EXPOSE 8080

# Launch with the prod profile so H2 console is disabled and logging is minimal.
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
