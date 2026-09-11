# Ambiente de desenvolvimento — VS Code

Este projeto usa **Java 21**, Maven, Docker e VS Code. O Docker já está instalado neste computador; ainda é necessário instalar/selecionar um JDK 21 e instalar Maven para executar a aplicação diretamente no terminal do VS Code.

## 1. Java 21 e Maven já preparados no projeto

O projeto contém uma instalação local oficial do Temurin JDK 21 e do Maven 3.9.16 em `.tools/`. Isso evita alterar o Java global do computador e já está configurado no workspace do VS Code.

Abra um **novo terminal integrado** depois de abrir a pasta e confirme:

```bash
java -version
mvn -version
```

Os resultados devem indicar Java 21 e Maven 3.9.16 usando Java 21. O Java 17 global continua intacto para outros projetos.

## 2. Abrir o projeto

No VS Code, use **File → Open Folder** e selecione:

```text
/home/rsjesus1/Documents/Codex/2026-09-10/quarkus-java-21-com-records-testes
```

Ao abrir, aceite as extensões recomendadas. Elas ficam declaradas em `.vscode/extensions.json`.

## 3. Confirmar o Java selecionado pelo VS Code

1. Abra a Paleta de Comandos (`Ctrl+Shift+P`).
2. Execute `Java: Configure Java Runtime`.
3. Em **JavaSE-21**, escolha o JDK 21 instalado como padrão do projeto.
4. Use `Java: Clean Java Language Server Workspace` se o editor continuar mostrando Java 17.

## 4. Usar as tarefas prontas

Abra **Terminal → Run Task** e use uma destas tarefas:

- `Quarkus: iniciar em desenvolvimento` — sobe a API com hot reload.
- `Qualidade: testes e cobertura` — executa `mvn verify`; falha se a cobertura ficar abaixo de 85%.
- `Docker: subir API e banco` — inicia API e PostgreSQL sem depender do Maven/JDK instalados no host.
- `Docker: parar ambiente` — encerra os containers.

## Checklist de pronto

```bash
java -version     # Java 21
mvn -version      # Maven 3.9+ usando Java 21
docker --version
docker compose version
mvn verify        # 13 testes e JaCoCo aprovado
```

Depois disso, o primeiro estudo será executar `Quarkus: iniciar em desenvolvimento`, criar uma tarefa via `curl` e acompanhar o fluxo `Resource → Service → Repository`.
