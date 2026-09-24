# Trilha 2 — Evoluindo a API de tarefas

Este roteiro continua a trilha inicial do README, concluída durante os estudos. As funcionalidades abaixo são propostas de exercícios: este documento não significa que elas já foram implementadas.

**Ponto de partida:** Java 21, Quarkus 3.17.7, CRUD de tarefas, classes em inglês e métodos e variáveis em português, testes unitários e HTTP, relatório JaCoCo e fluxo de pull request.

**Próximo exercício:** etapa 1, filtro de tarefas. Faça uma etapa por vez e registre aqui as dúvidas e decisões.

## Acompanhamento

Marque uma etapa somente depois de implementar, testar e revisar o PR.

- [ok] 1. Filtro de tarefas concluídas e pendentes.
- [ ] 2. Paginação e ordenação.
- [ ] 3. Validações e respostas de erro.
- [ ] 4. Testes do fluxo completo com PostgreSQL.
- [ ] 5. Migrações de banco com Flyway.
- [ ] 6. Autenticação e autorização.

## Como trabalhar em cada etapa

1. Descreva a entrada e a resposta esperadas antes de alterar o código.
2. Confira `git status` e crie uma branch a partir da branch principal atualizada. Preserve alterações locais ainda não concluídas; não as descarte para trocar de branch.
3. Escreva um teste para o comportamento novo e confirme que ele falha pelo motivo esperado.
4. Implemente a menor alteração que faça esse teste passar.
5. Melhore a clareza do código mantendo os testes passando.
6. Execute `mvn clean verify` e exercite os cenários no Postman.
7. Revise `git diff`, prepare apenas os arquivos da etapa e confira `git diff --cached`.
8. Faça commit, envie a branch, abra o PR e aguarde os checks antes do merge.
9. Anote o que aprendeu e marque a etapa concluída.

Use nomes de branches sem acentos. As sugestões abaixo não são branches já criadas.

## 1. Filtro de tarefas

**Objetivo:** consultar todas as tarefas, somente as concluídas ou somente as pendentes.

**Branch sugerida:** `estudo-filtro-tarefas`.

### Contrato proposto

| Requisição | Resultado esperado |
| --- | --- |
| `GET /tasks` | Todas as tarefas, mantendo o comportamento atual. |
| `GET /tasks?concluida=true` | Somente tarefas concluídas. |
| `GET /tasks?concluida=false` | Somente tarefas pendentes. |
| Consulta sem resultados | HTTP 200 com uma lista vazia. |

O parâmetro novo `concluida` não renomeia o campo JSON existente `completed`. Preserve também os demais campos JSON e as rotas atuais.

### Conceitos para aprender

- Parâmetro de consulta: a parte da URL depois de `?`.
- `boolean` aceita `true` ou `false`; `Boolean` também aceita `null`.
- Neste exercício, `null` representa ausência de filtro, não uma tarefa com estado indefinido.
- O recurso recebe HTTP, o serviço organiza a operação e o repositório consulta o banco.
- Consultas parametrizadas: passar valores como parâmetros em vez de montar uma expressão por concatenação.

### Exercício

1. Leia `TaskResource.listarTodas`, `TaskService.listarTodas` e `TaskRepository`.
2. Defina como receber o filtro opcional e encaminhá-lo até a consulta.
3. Prepare tarefas concluídas e pendentes para os testes.
4. Implemente os três comportamentos da tabela.
5. Compare os resultados no Postman.

### Critérios de conclusão

- [ok] Ausência de filtro continua retornando todas as tarefas.
- [ok] `true` e `false` produzem resultados diferentes quando existem tarefas dos dois tipos.
- [ok] Consulta vazia retorna uma lista vazia.
- [ok] Testes verificam o encaminhamento do filtro e os resultados esperados.
- [ok] Você consegue explicar por que usar `Boolean` nesse parâmetro.

**Perguntas para revisão:** O que aconteceria com `boolean` quando o filtro não fosse informado? Por que um teste HTTP com serviço simulado não prova que a consulta ao banco está correta?

## 2. Paginação e ordenação

**Objetivo:** limitar a quantidade de registros por consulta e definir sua ordem.

**Branch sugerida:** `estudo-paginacao-tarefas`.

**Exemplo proposto:** `GET /tasks?pagina=0&tamanho=10&ordenarPor=titulo`.

### Conceitos e decisões

- Página, tamanho, deslocamento e total de registros.
- Adote páginas iniciando em zero e documente isso no contrato.
- Defina tamanho padrão e limite máximo; rejeite página negativa e tamanho inválido.
- Permita somente campos de ordenação conhecidos; não use texto arbitrário do cliente na consulta.
- Use um desempate estável, como o ID, para evitar resultados inconsistentes entre páginas.
- Decida como comunicar totais ao cliente. Trocar uma lista por um objeto com metadados altera o contrato: planeje a compatibilidade antes de fazer isso.

### Exercícios e conclusão

- [ ] Consultar primeira página, próxima página e uma página sem resultados.
- [ ] Testar parâmetros inválidos e limite máximo.
- [ ] Combinar paginação com o filtro da etapa 1.
- [ ] Conferir ordenação e desempate.
- [ ] Documentar exemplos e qualquer decisão de evolução da resposta.

**Pergunta para revisão:** Por que carregar todas as tarefas e recortar a lista em Java não resolve o custo da consulta ao banco?

## 3. Validações e respostas de erro

**Objetivo:** compreender e testar as validações existentes e tornar os erros previsíveis.

**Branch sugerida:** `estudo-validacoes-api`.

O projeto já possui restrições na requisição e tratamento para tarefa não encontrada. Comece por esses comportamentos antes de adicionar regras.

### Cenários

| Entrada ou operação | Expectativa a verificar |
| --- | --- |
| Título vazio ou somente espaços | HTTP 400. |
| Título acima de 120 caracteres | HTTP 400. |
| Descrição acima de 500 caracteres | HTTP 400. |
| JSON malformado | HTTP 400. |
| Busca, atualização ou exclusão de ID inexistente | HTTP 404. |
| Requisição válida | Continua funcionando. |

- [ ] Testar limites exatos e valores imediatamente acima deles.
- [ ] Diferenciar validação da entrada de regra de negócio.
- [ ] Definir e testar quais campos o cliente recebe nas respostas de erro.
- [ ] Preservar os contratos já utilizados pelo Postman ao evoluir o formato.

**Perguntas para revisão:** Qual a diferença entre `400` e `404`? O erro ocorreu antes de chegar ao serviço? Um status correto com mensagem incorreta deve passar no teste?

## 4. Testes do fluxo completo com PostgreSQL

**Objetivo:** verificar HTTP → recurso → serviço → repositório → banco sem substituir o serviço por mock.

**Branch sugerida:** `estudo-integracao-postgresql`.

Hoje, `TaskServiceTest` usa repositório simulado e `TaskResourceTest` usa serviço simulado. `TaskCompatibilityTest` já verifica JSON e persistência no H2. O próximo passo é exercitar o fluxo completo no mesmo tipo de banco usado pela aplicação: PostgreSQL.

### Exercício

1. Entenda a função de um banco descartável de testes.
2. Avalie Quarkus Dev Services ou Testcontainers e escolha uma abordagem para o PostgreSQL de teste.
3. Separe essa configuração dos dados de desenvolvimento. Os testes devem usar exclusivamente um banco descartável.
4. Faça POST, capture o ID, consulte por GET, atualize e exclua.
5. Verifique os dados retornados em cada etapa e a resposta 404 após a exclusão.
6. Garanta isolamento: um teste não pode depender da execução de outro.

### Critérios de conclusão

- [ ] O fluxo usa serviço e repositório reais.
- [ ] A gravação é conferida por uma consulta posterior.
- [ ] Os testes passam individualmente e em conjunto.
- [ ] A configuração funciona localmente e no CI, com acesso ao runtime de containers necessário.
- [ ] Nenhum teste usa ou limpa o banco de desenvolvimento.

**Pergunta para revisão:** Qual defeito de SQL, transação ou mapeamento poderia passar em um teste com mock e falhar aqui?

## 5. Migrações com Flyway

**Objetivo:** controlar a evolução do banco com scripts versionados.

**Branch sugerida:** `estudo-migracoes-flyway`.

### Conceitos

- Migração: uma alteração identificada e ordenada no esquema do banco.
- Histórico: registro das migrações já aplicadas.
- Exemplo de nome: `V1__criar_tabela_tarefas.sql`.
- Migrações já aplicadas devem ser preservadas; mudanças posteriores recebem uma nova versão.

### Exercício

1. Inspecione o esquema existente, incluindo tabela, colunas e geração de IDs.
2. Planeje a transição da atualização automática do Hibernate para migrações.
3. Teste primeiro em um banco novo e descartável.
4. Crie uma segunda migração, por exemplo adicionando uma coluna opcional de data de criação.
5. Planeje separadamente a adoção em um banco já populado; não aplique a criação inicial cegamente sobre tabelas existentes.

### Critérios de conclusão

- [ ] Um banco vazio é preparado pelas migrações.
- [ ] Reiniciar a aplicação não reaplica alterações já executadas.
- [ ] A segunda migração preserva dados de teste existentes.
- [ ] O histórico pode ser consultado e explicado.
- [ ] A estratégia para o banco atual está documentada antes de qualquer aplicação nele.

**Pergunta para revisão:** Por que editar a primeira migração depois que outra pessoa já a executou pode causar problemas?

## 6. Autenticação e autorização

**Objetivo:** identificar o usuário e garantir que ele acesse somente as tarefas permitidas.

**Branch sugerida:** `estudo-seguranca-tarefas`.

### Conceitos

- Autenticação: quem está fazendo a requisição?
- Autorização: o que essa pessoa pode fazer?
- Token: credencial apresentada à API; estude a validação por uma integração suportada, como OIDC, antes de implementar segurança por conta própria.
- A associação entre tarefa e usuário também exige planejamento do banco e das migrações.

### Exercícios e conclusão

- [ ] Definir como obter a identidade do usuário autenticado.
- [ ] Criar uma tarefa vinculada a essa identidade, sem confiar em um ID de usuário enviado livremente no JSON.
- [ ] Filtrar listagens pelo proprietário.
- [ ] Impedir leitura, edição e exclusão de tarefas de outro usuário.
- [ ] Testar usuário A, usuário B e ausência de autenticação.
- [ ] Entender a diferença entre 401 e 403 e documentar a política para recursos de outro usuário.
- [ ] Manter credenciais e tokens fora dos arquivos versionados.

**Pergunta para revisão:** Proteger apenas o endpoint de listagem impede que alguém tente acessar diretamente `/tasks/123`?

## Comandos de consulta

Execute na raiz do projeto. Para outros comandos enquanto o Quarkus está aberto, use outro terminal.

```bash
# Executar todos os testes e verificar a cobertura sem dados antigos
mvn clean verify

# Executar apenas os testes unitários do serviço
mvn -Dtest=TaskServiceTest test

# Executar um cenário específico já existente
mvn -Dtest=TaskServiceTest#deveBuscarTarefaPorId test

# Abrir o relatório de cobertura no Ubuntu
xdg-open target/site/jacoco/index.html

# Iniciar a API em desenvolvimento com log de requisições HTTP
mvn quarkus:dev -Dquarkus.http.access-log.enabled=true

# Conferir as alterações antes do commit
git status
git diff
git diff --cached
```

`clean` remove os resultados gerados em `target`, não os fontes. Evite executá-lo ao mesmo tempo que outra execução Maven no mesmo projeto. No visualizador de `git diff`, pressione `q` para sair; no Quarkus, use Ctrl+C para encerrar.

### Como ler o resultado dos testes

- `Tests run`: quantidade de testes executados.
- `Failures`: verificações que não produziram o resultado esperado.
- `Errors`: erros inesperados durante a execução ou preparação.
- `Skipped`: testes reconhecidos que não foram executados.
- `All coverage checks have been met`: o mínimo configurado de cobertura foi atendido.
- `BUILD SUCCESS`: todas as etapas executadas pelo comando terminaram com sucesso.

O projeto exige 85% de cobertura de linhas. Testes passando não garantem cobertura suficiente; cobertura alta não garante boas verificações. O objetivo é testar comportamentos relevantes, não apenas executar linhas.

## Registro de estudo e dúvidas

Copie este modelo a cada etapa:

```text
Etapa:
Branch / PR:
Comportamento que estou implementando:
Arquivo e método que estou estudando:
O que já entendi:
Minha dúvida:
Comando ou requisição executada:
Resultado esperado:
Resultado observado / trecho do erro:
Decisão tomada e motivo:
Testes executados:
Próximo passo:
```

Para retomar a conversa, você pode dizer: “Estou na etapa 1 da trilha 2, no método listarTodas. Não entendi como a ausência de filtro chega ao repositório”. Inclua o trecho relevante e a mensagem de erro, se houver, sem credenciais.

## Quando avançar

Avance quando conseguir explicar o caminho da requisição, demonstrar os cenários no Postman, apontar os testes que os verificam e revisar o próprio diff. Cada PR deve apresentar o comportamento antes e depois, as decisões de compatibilidade e a validação realizada.
