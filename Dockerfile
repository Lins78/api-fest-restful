# Dockerfile otimizado para Java 21
# Multi-stage build para otimizar o tamanho da imagem

# Estágio de build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Baixar dependências (para aproveitamento de cache)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN ./mvnw clean package -DskipTests

# Estágio de runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

# Instalar dumb-init para signal handling
RUN apk add --no-cache dumb-init

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

WORKDIR /app

# Copiar JAR compilado do estágio anterior
COPY --from=builder /app/target/*.jar app.jar

# Mudar para usuário não-root
USER appuser

# Expor porta
EXPOSE 8080

# Configurar JVM para Java 21 com otimizações
ENV JAVA_OPTS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions -Xmx512m -Xms256m"

# Usar dumb-init para signal handling adequado
ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]