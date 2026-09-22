# Tarefa API — trilha prática de Quarkus

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
mvn clean verify
```

O relatório é gerado em `target/site/jacoco/index.html`. A fase `verify` falha abaixo de **85%** de cobertura de linhas: isso é um *quality gate*, o mesmo tipo de proteção executada no CI.

## Roteiro de estudo no próprio projeto

1. Rode a API e faça um `POST`/`GET` com `curl` ou Postman.
2. Leia `TaskRequest` e `TaskResponse`; troque um `record` por classe comum para entender o código que ele elimina.
3. Leia um teste de `TaskServiceTest`: ele usa Mockito e não depende de banco nem Quarkus inicializado.
4. Leia `TaskResourceTest`: ele testa HTTP usando Quarkus e substitui o serviço por mock.
5. Remova temporariamente um teste e rode `mvn clean verify` para observar o gate de cobertura bloquear o build.
6. Leia o `Dockerfile`: a primeira etapa compila, a segunda apenas executa a aplicação.
7. Faça um commit e abra um pull request para acompanhar as etapas no GitHub Actions.

## Próxima trilha de estudos

A trilha inicial foi concluída. Continue pelo [roteiro da Trilha 2 — Evoluindo a API de tarefas](docs/TRILHA_ESTUDOS_02.md).

O roteiro contém exercícios, critérios de conclusão, perguntas para revisão e um modelo para registrar dúvidas. As novas funcionalidades são propostas de estudo, ainda a implementar.

1. Filtro de tarefas concluídas e pendentes — próximo exercício.
2. Paginação e ordenação.
3. Validações e respostas de erro.
4. Testes do fluxo completo com PostgreSQL.
5. Migrações com Flyway.
6. Autenticação e autorização.

Trabalhe uma etapa por branch e pull request, verificando testes e cobertura antes de avançar.

## Convenção de nomes e compatibilidade

As classes e os records da aplicação e dos testes usam nomes em inglês. Métodos, variáveis e componentes dos records permanecem em português, sem acentos nos identificadores. Os pacotes existentes também permanecem em português.
O fluxo principal é `TaskResource → TaskService → TaskRepository → Task`.
`TaskRequest` recebe os dados e `TaskResponse` representa a resposta.
`ApiExceptionMapper` converte `TaskNotFoundException` em uma resposta HTTP 404.

Os pacotes ficam em `br.com.estudo.tarefa`, com as camadas `api`, `servico`, `repositorio` e `dominio`.
`TaskCompatibilityTest` confere os nomes JSON e o mapeamento da tabela usando o banco H2 de testes; a gravação de teste é revertida ao terminar.
Para executar apenas os testes unitários, use `mvn -Dtest=TaskServiceTest test`.
Para executar um cenário, use `mvn -Dtest=TaskServiceTest#deveBuscarTarefaPorId test`.

Os nomes externos permanecem compatíveis com o Postman e o banco:

- Rotas `/tasks` e JSON `id`, `title`, `description`, `completed`, `code` e `message`.
- `@JsonProperty` relaciona os componentes dos records em português aos campos JSON existentes.
- `@Column`, `@Table` e o nome explícito em `@Entity` preservam o mapeamento de persistência.
- `id` é herdado de `PanacheEntity`; métodos como `persist`, `listAll` e `toResponse` (do mapeador de exceções) são contratos das bibliotecas.
- O sufixo `Test` permite que o Maven encontre os testes automaticamente. Anotações, palavras-chave Java e APIs de bibliotecas mantêm seus nomes oficiais.
- Coordenadas Maven, configurações do Quarkus, Docker, variáveis de ambiente e GitHub Actions mantêm seus identificadores de integração.
