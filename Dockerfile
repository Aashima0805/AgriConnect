FROM maven:3.9.11-eclipse-temurin-24

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

EXPOSE 8081

CMD ["sh", "-c", "java -jar target/*.jar"]