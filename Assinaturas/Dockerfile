# Construção da aplicação
FROM openjdk:17-jdk-slim as build

# Diretório de trabalho dentro do container
WORKDIR /app

# Copiar o arquivo JAR da aplicação para o container
COPY target/Assinaturas-0.0.1-SNAPSHOT.jar app.jar

# Etapa 2: Execução da aplicação
FROM openjdk:17-jdk-slim

# Copiar o arquivo JAR do estágio de construção
COPY --from=build /app/app.jar /app.jar

# Comando para iniciar a aplicação
CMD ["java", "-jar", "/app.jar"]

# Expõe a porta para a aplicação
EXPOSE 8080
