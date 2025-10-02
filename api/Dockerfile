FROM openjdk:17-jdk-slim

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar arquivos do Maven
COPY pom.xml .
COPY src ./src

# Instalar Maven e compilar
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get remove -y maven && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

EXPOSE 8080

CMD ["java", "-jar", "target/creditos-api-0.0.1-SNAPSHOT.jar"]
