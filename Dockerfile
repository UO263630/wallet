# Usa una imagen base que tenga Java 17 instalado
FROM openjdk:17-jdk

# Copia el archivo JAR construido de tu proyecto a la imagen
COPY target/*.jar app.jar 

# Expone el puerto en el que tu aplicación se ejecutará dentro del contenedor
EXPOSE 8080

# Define el comando para ejecutar tu aplicación Spring Boot cuando el contenedor se inicie
ENTRYPOINT ["java", "-jar", "/app.jar"]