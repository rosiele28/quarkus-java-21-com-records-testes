# Tarefa API — trilha prática de Quarkus

Uma API REST de tarefas criada para estudar o fluxo que você encontrará em um time Java: Java 21, `record`, Quarkus, PostgreSQL, testes, cobertura, Docker e GitHub Actions.

## O que cada parte ensina

| Parte | Onde observar | Por que importa |
| --- | --- | --- |
| Java 21 `record` | `RequisicaoTarefa` e `RespostaTarefa` | DTOs imutáveis, curtos e sem boilerplate. |
| Quarkus REST | `RecursoTarefa` | Endpoints HTTP e códigos de resposta corretos. |
| Regra de negócio | `ServicoTarefa` | Separar HTTP da lógica que merece teste unitário. |
| Banco de dados | `Tarefa` e `RepositorioTarefa` | PostgreSQL com Hibernate/Panache. |
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
2. Leia `RequisicaoTarefa` e `RespostaTarefa`; troque um `record` por classe comum para entender o código que ele elimina.
3. Leia um teste de `ServicoTarefaTest`: ele usa Mockito e não depende de banco nem Quarkus inicializado.
4. Leia `RecursoTarefaTest`: ele testa HTTP usando Quarkus e substitui o serviço por mock.
5. Remova temporariamente um teste e rode `mvn clean verify` para observar o gate de cobertura bloquear o build.
6. Leia o `Dockerfile`: a primeira etapa compila, a segunda apenas executa a aplicação.
7. Faça um commit e abra um pull request para acompanhar as etapas no GitHub Actions.

## Próxima evolução realista

Depois de entender o esqueleto, adicione paginação, filtro por status, migrações com Flyway, autenticação e testes de integração reais com Testcontainers. Não comece por esses itens: primeiro faça o fluxo atual funcionar e saiba explicá-lo.

## Nomes em português e compatibilidade

O código Java da aplicação e dos testes usa nomes em português, sem acentos nos identificadores.
O fluxo principal é `RecursoTarefa → ServicoTarefa → RepositorioTarefa → Tarefa`.
`RequisicaoTarefa` recebe os dados e `RespostaTarefa` representa a resposta.
`MapeadorExcecaoApi` converte `TarefaNaoEncontradaException` em uma resposta HTTP 404.

Os pacotes ficam em `br.com.estudo.tarefa`, com as camadas `api`, `servico`, `repositorio` e `dominio`.
`CompatibilidadeTarefaTest` confere os nomes JSON e o mapeamento da tabela usando o banco H2 de testes; a gravação de teste é revertida ao terminar.
Para executar apenas os testes unitários, use `mvn -Dtest=ServicoTarefaTest test`.
Para executar um cenário, use `mvn -Dtest=ServicoTarefaTest#deveBuscarTarefaPorId test`.

Os nomes externos permanecem compatíveis com o Postman e o banco:

- Rotas `/tasks` e JSON `id`, `title`, `description`, `completed`, `code` e `message`.
- `@JsonProperty` relaciona os nomes Java em português aos campos JSON existentes.
- `@Column`, `@Table` e o nome explícito em `@Entity` preservam o mapeamento de persistência.
- `id` é herdado de `PanacheEntity`; métodos como `persist`, `listAll` e `toResponse` (do mapeador de exceções) são contratos das bibliotecas.
- O sufixo `Test` permite que o Maven encontre os testes automaticamente. Anotações, palavras-chave Java e APIs de bibliotecas mantêm seus nomes oficiais.
- Coordenadas Maven, configurações do Quarkus, Docker, variáveis de ambiente e GitHub Actions mantêm seus identificadores de integração.
