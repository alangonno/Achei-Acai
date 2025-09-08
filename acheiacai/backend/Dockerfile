
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do contentor
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:11-jre21-temurin

# Apagamos a aplicação de exemplo que vem com o Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

COPY --from=builder /app/target/achei-acai-api.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]