FROM maven:3.9.0-eclipse-temurin-17 as build

# Definindo o diretório de trabalho dentro do container
WORKDIR /app

# Copiando os arquivos de projeto para o container
COPY . .

# Executando o comando de build com Maven para gerar o JAR
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

# Definindo o diretório de trabalho no container final
WORKDIR /app

# Copiando o JAR gerado do estágio de build anterior para o container final
COPY --from=build /app/target/desafiovotacao-0.0.1-SNAPSHOT.jar /app/desafiovotacao-0.0.1-SNAPSHOT.jar

# Definindo o comando de entrada para rodar a aplicação
CMD ["java", "-jar", "/app/desafiovotacao-0.0.1-SNAPSHOT.jar"]
