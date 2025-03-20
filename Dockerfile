# Build stage
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app

# Копируем только pom.xml сначала
COPY pom.xml .
# Скачиваем зависимости отдельно (для кэширования)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src
# Собираем приложение
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"] 