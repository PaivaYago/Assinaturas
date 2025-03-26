# Usa uma imagem base do Java 17
FROM openjdk:17-jdk-slim

# Copia o arquivo JAR da aplicação para o container
COPY target/*.jar app.jar

# Expõe a porta que a aplicação usa
EXPOSE 8080

# Comando para executar a aplicação
CMD ["java", "-jar", "app.jar"]