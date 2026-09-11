# Task API — trilha prática de Quarkus

Uma API REST de tarefas criada para estudar o fluxo que você encontrará em um time Java: Java 21, `record`, Quarkus, PostgreSQL, testes, cobertura, Docker e GitHub Actions.

## O que cada parte ensina

| Parte | Onde observar | Por que importa |
| --- | --- | --- |
| Java 21 `record` | `TaskRequest` e `TaskResponse` | DTOs imutáveis, curtos e sem boilerplate. |
| Quarkus REST | `TaskResource` | Endpoints HTTP e códigos de resposta corretos. |
| Regra de negócio | `TaskService` | Separar HTTP da lógica que merece teste unitário. |
| Banco de dados | `Task` e `TaskRepository` | PostgreSQL com Hibernate/Panache. |
| Qualidade | `src/test` e JaCoCo no `pom.xml` | Build falha se a cobertura de linhas for menor que 85%. |
| Empacotamento | `Dockerfile` e `compose.yaml` | Mesmo ambiente para desenvolvimento e entrega. |
| CI | `.github/workflows/ci.yml` | Testes, cobertura e imagem a cada pull request/push. |

## Pré-requisitos locais

- JDK 21
- Maven 3.9+ (ou use somente Docker)
- Docker e Docker Compose

## Subir tudo com Docker

```bash
docker compose up --build
```

A API ficará em `http://localhost:8080`. O PostgreSQL fica em `localhost:5432` para inspeção local.

## Exercitar a API

```bash
curl -X POST http://localhost:8080/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Estudar Quarkus","description":"Criar minha primeira API","completed":false}'

curl http://localhost:8080/tasks
```

## Testar e checar cobertura

```bash
mvn verify
```

O relatório é gerado em `target/site/jacoco/index.html`. A fase `verify` falha abaixo de **85%** de cobertura de linhas: isso é um *quality gate*, o mesmo tipo de proteção executada no CI.

## Roteiro de estudo no próprio projeto

1. Rode a API e faça um `POST`/`GET` com `curl` ou Postman.
2. Leia `TaskRequest` e `TaskResponse`; troque um `record` por classe comum para entender o código que ele elimina.
3. Leia um teste de `TaskServiceTest`: ele usa Mockito e não depende de banco nem Quarkus inicializado.
4. Leia `TaskResourceTest`: ele testa HTTP usando Quarkus e substitui o serviço por mock.
5. Remova temporariamente um teste e rode `mvn verify` para observar o gate de cobertura bloquear o build.
6. Leia o `Dockerfile`: a primeira etapa compila, a segunda apenas executa a aplicação.
7. Faça um commit e abra um pull request para acompanhar as etapas no GitHub Actions.

## Próxima evolução realista

Depois de entender o esqueleto, adicione paginação, filtro por status, migrações com Flyway, autenticação e testes de integração reais com Testcontainers. Não comece por esses itens: primeiro faça o fluxo atual funcionar e saiba explicá-lo.
