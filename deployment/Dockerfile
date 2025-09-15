# Etapa de build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar Gradle Wrapper y configuración
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY . .

# Dar permisos de ejecución
RUN chmod +x gradlew


# Compilar (salta tests por velocidad)
RUN ./gradlew clean build -x test -x validateStructure

# ============================================
# Etapa de runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Corregir esta línea para apuntar al JAR del módulo app-service
COPY --from=build /app/applications/app-service/build/libs/Authentication-Service.jar app.jar

RUN chown appuser:appgroup app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
USER appuser

EXPOSE 8090

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
