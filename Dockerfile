# ===============================================================================
# DOCKERFILE CORRIGIDO - API FEST RESTFUL
# ===============================================================================

# ========== BUILD STAGE ==========
FROM amazoncorretto:21-alpine AS builder

LABEL maintainer="API FEST Team"
LABEL description="Spring Boot API Build Stage"

RUN apk add --no-cache curl tar bash maven

WORKDIR /build

COPY pom.xml .
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn

RUN mvn dependency:go-offline -B -q || ./mvnw dependency:go-offline -B -q

COPY src src

RUN mvn clean package -DskipTests -B -q || ./mvnw clean package -DskipTests -B -q

# ========== PRODUCTION STAGE ==========
FROM amazoncorretto:21-alpine AS production

LABEL stage="production"

RUN apk add --no-cache dumb-init curl \
    && addgroup -g 1001 appgroup \
    && adduser -S appuser -u 1001 -G appgroup

WORKDIR /app
USER appuser:appgroup

COPY --from=builder --chown=appuser:appgroup /build/target/*.jar app.jar

ENV JVM_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport"
ENV SPRING_PROFILES_ACTIVE="prod"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JVM_OPTS -jar app.jar"]