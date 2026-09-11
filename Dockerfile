# Etapa de build: permite compilar com Java 21 mesmo que a máquina host tenha outra versão.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package -DskipTests

# Etapa de execução: imagem final menor, contendo apenas o necessário para rodar a API.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /work
COPY --from=build /build/target/quarkus-app/lib/ /work/lib/
COPY --from=build /build/target/quarkus-app/*.jar /work/
COPY --from=build /build/target/quarkus-app/app/ /work/app/
COPY --from=build /build/target/quarkus-app/quarkus/ /work/quarkus/
EXPOSE 8080
CMD ["java", "-jar", "/work/quarkus-run.jar"]
