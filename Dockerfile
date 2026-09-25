FROM eclipse-temurin:24-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests

EXPOSE 8081

CMD ["sh", "-c", "java -jar target/*.jar"]