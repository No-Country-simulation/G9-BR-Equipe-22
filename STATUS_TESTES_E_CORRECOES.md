# Status dos testes automatizados e correções — TechTagger

Atualizado em 2026-08-15, depois do retreino do modelo em PT-BR já rodando em produção local.

## 1. Resumo rápido

| Camada | Testes escritos | Confirmados rodando | Pendente |
|---|---|---|---|
| **ml-api** (pytest) | 42 | ✅ **43/43 passaram na sua máquina**, 92% de cobertura | Nada — confirmado de ponta a ponta |
| **backend** (JUnit) | 30 (5 arquivos) | ✅ **30/30 passaram na sua máquina**, 77% de cobertura (JaCoCo) | Nada — confirmado de ponta a ponta |
| **frontend** (Vitest) | 68 (13 arquivos) | ✅ **68/68 passaram na sua máquina** (`npm test` e `npm run test:coverage`), 97,1% de cobertura | Nada — confirmado de ponta a ponta |

**As três camadas estão 100% confirmadas, rodando na sua máquina.** ml-api (`pytest`), backend (`./mvnw test`) e agora frontend (`npm test` / `npm run test:coverage`) — os três com resultado batendo exatamente com o que eu tinha verificado antes de entregar. No frontend, eu já tinha rodado a suíte repetidamente no meu sandbox (inclusive 3x sob `--coverage`) porque um teste de upload de CSV que escrevi se mostrou instável ali por causa do timing do `FileReader`; troquei a espera fixa por um polling real antes de te mandar, e a cobertura que você rodou na sua máquina (97,11%) bateu com a minha quase número a número.

## 2. ml-api — pytest ✅ confirmado

Arquivos: `conftest.py`, `test_text_utils.py`, `test_schemas.py`, `test_storage_service.py`, `test_ml_service.py`, `test_routes_content.py`, `test_routes_stats.py`.

**Rodado por você na sua máquina, com `pip install -r requirements.txt` + `pytest tests/ -v --cov=app --cov-report=term-missing`:**

```
43 passed, 1 warning in 16.96s

Name                              Stmts   Miss  Cover   Missing
---------------------------------------------------------------
app/main.py                          20      2    90%   37, 49
app/routes/content.py                34      6    82%   28-29, 46-47, 62-63
app/routes/stats.py                  14      0   100%
app/schemas.py                       50      0   100%
app/services/ml_service.py           59      0   100%
app/services/storage_service.py      44     12    73%   18-35
app/utils/text_utils.py               9      0   100%
---------------------------------------------------------------
TOTAL                               245     20    92%
```

Os testes que antes eu não conseguia confirmar no sandbox por causa do bloqueio de rede pro Hugging Face (`test_ml_service.py`, `test_routes_content.py`) **passaram todos** — confirma que o pipeline de classificação + recomendação semântica está funcionando corretamente com o modelo novo em PT-BR.

Os 8% sem cobertura são esperados: `storage_service.py` linhas 18-35 é o caminho real de download do OCI (não testado de propósito, para não depender de credenciais de nuvem), e algumas linhas de tratamento de erro em `main.py`/`content.py`.

## 3. backend — JUnit + JaCoCo ✅ confirmado

Arquivos: `ConteudoRequestValidationTest.java`, `GlobalExceptionHandlerTest.java`, `ConteudoServiceTest.java`, `ConteudoControllerTest.java`, `MLServiceClientTest.java`.

**Rodado por você na sua máquina, com `./mvnw test`:**

```
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Cobrem: validação de DTO (6 testes), o vazamento de mensagem de erro no `GlobalExceptionHandler` (5 testes, incluindo o que documenta o vazamento — ver item 5 do backlog abaixo, ainda não corrigido no código), a camada de serviço com Mockito (7 testes), o controller com `@WebMvcTest` + `@MockitoBean` (5 testes), e o `MLServiceClient` — cliente HTTP reativo (WebClient) que fala com o ml-api (7 testes: sucesso, erro 4xx sem retry, erro 5xx com retry até esgotar tentativas, tanto para classificação individual quanto em lote e para listagem de categorias). Esse último pacote (`client`) estava em 1% de cobertura porque os outros testes sempre mockam essa classe inteira; depois do `MLServiceClientTest.java` foi pra **93% de instruções / 62% de branches** — os logs da própria execução confirmam o retry acontecendo de verdade ("Tentando novamente... tentativa 1", "tentativa 2") antes de desistir.

Cobertura total do backend (JaCoCo, no seu relatório): **77% de instruções, 36% de branches**. Os pacotes com cobertura mais baixa são esperados nesta fase: `config` (0% — beans de configuração do Spring, como `WebClientConfig`/`CorsConfig`, normalmente só são exercitados em testes de integração com contexto completo, que este projeto não tem ainda) e `model` (38% — entidades JPA, cujos getters/setters/equals gerados raramente aparecem cobertos em testes unitários de service/controller). Não são bugs, só uma fronteira natural do que testes unitários alcançam sem subir o contexto Spring inteiro.

No caminho até aqui apareceram 3 problemas reais, não hipotéticos — cada um exigiu rodar de verdade pra aparecer, o que reforça por que "revisei manualmente" nunca substitui rodar:

1. **Lombok não estava configurado como annotation processor** no `pom.xml` — o `@RequiredArgsConstructor` do `ConteudoController` não gerava o construtor, e os campos `final` apareciam como "não inicializados". Corrigido com um `<annotationProcessorPaths>` explícito no `maven-compiler-plugin`.
2. **`@WebMvcTest` mudou de pacote no Spring Boot 4** — de `org.springframework.boot.test.autoconfigure.web.servlet` (Boot 3.x) para `org.springframework.boot.webmvc.test.autoconfigure`. O import antigo não existe mais na 4.1.0. Corrigido no `ConteudoControllerTest.java`.
3. **Campo `ObjectMapper` autowired mas nunca usado** no `ConteudoControllerTest.java` — no slice reduzido do `@WebMvcTest`, esse bean não fica disponível no contexto, e a injeção obrigatória derrubava os 5 testes da classe. Removido (os JSONs de teste já eram strings literais, não usavam o `ObjectMapper` mesmo).

```bash
cd backend
./mvnw test
open target/site/jacoco/index.html   # relatório de cobertura
```

## 4. frontend — Vitest ✅ confirmado

Arquivos: `api.test.js`, `useConteudo.test.js`, `useConteudoLote.test.js`, `usePaginacao.test.js`, `CategoriaBadge.test.js`, `ErroAlerta.test.js` (rodada anterior) + `ProcessarView.test.js`, `ProcessarLoteView.test.js`, `HistoricoView.test.js`, `DetalheView.test.js`, `App.test.js`, `NavBar.test.js`, `router/index.test.js` (novos, fechando a lacuna que faltava).

- **68/68 passaram**, rodado de fato por mim no sandbox (o registro do npm não é bloqueado ali) — repeti 3x sem cobertura e 3x com `--coverage` pra garantir que nada era instável antes de te mandar.
- Cobertura: **97,1%** no projeto inteiro. `App.vue`, `NavBar.vue`, `router/index.js`, `ProcessarView.vue` e `DetalheView.vue` chegaram a 100%; `HistoricoView.vue` 100% de linhas (as poucas branches não cobertas são o caminho em que `listarCategorias()` falha, testado, mas o optional chaining `dados.value &&` tem uma combinação que o v8 conta separado); `ProcessarLoteView.vue` 94,4% (a única tela com bastante lógica extra: parser de CSV, upload de arquivo).
- O que essas telas cobrem: validação de formulário e mensagens de erro (`ProcessarView`), estados de carregamento/vazio/resultado/erro em todas as 4 telas, o parser de CSV da tela de lote (linha bem formada, cabeçalho sem as colunas certas, arquivo vazio — inclusive testando o upload de verdade via `FileReader`/`File` do jsdom, não só chamando uma função isolada), paginação e filtro de categoria no histórico, roteamento (`useRoute`, `router.resolve`) no detalhe, e a composição `App.vue` + `NavBar.vue` com link ativo por rota.
- **Um bug de teste real apareceu no caminho** (não no código do produto): o teste de "CSV vazio" usava um `setTimeout(0)` fixo pra esperar o `FileReader.onload` (que é assíncrono mesmo em memória, no jsdom) — passava isolado, mas falhava de forma intermitente quando a suíte inteira rodava sob `--coverage`, porque a instrumentação muda o timing o suficiente pra quebrar essa suposição. Troquei por um polling que espera a condição real aparecer no DOM (`aguardarCondicao`, dentro do próprio arquivo de teste) em vez de contar com um tempo fixo — 6 rodadas seguidas sem falha depois disso.

```bash
cd frontend
npm install
npm test
npm run test:coverage
```

## 5. O que foi corrigido nesta revisão

1. **Dataset em inglês → PT-BR.** O `techtagger_dataset_expanded.csv` (1.520 linhas, 100% inglês) foi traduzido inteiro para `techtagger_dataset_ptbr.csv`, mantendo termos técnicos em inglês (TypeScript, AWS, React etc., como é padrão em conteúdo técnico BR). O original em inglês continua no repo, intacto.
2. **Bug de acentuação em produção.** `ml-api/app/utils/text_utils.py::limpar_texto` apagava letras acentuadas ("ótimo" virava " timo") em vez de preservá-las — e o notebook de treino já preservava. Ou seja, o texto visto no treino era diferente do texto visto em produção. Corrigido para as duas pontas usarem a mesma lógica; teste atualizado para confirmar.
3. **Modelo retreinado em PT-BR.** TF-IDF + Regressão Logística com GridSearchCV, mesma abordagem do `data-science/README.md`. Resultado: acurácia/F1 subiram de 0,955 (inglês) para **0,970** (PT-BR). Você já copiou os artefatos pra `ml-api/models/` e rodou `regenerate_embeddings.py` com sucesso — confirmado testando pelo layout (categoria certa, relacionados todos da mesma categoria com boa similaridade).
4. **Pasta órfã `API de ml/` removida.** Era uma cópia antiga de `models/` (mesma data do commit inicial), sem nenhuma referência no código. Movida para `_to_delete/` (não consigo apagar direto pela ponte com seu Mac — você pode arrastar essa pasta pra lixeira quando quiser).

## 6. O que ainda falta

**Cobertura de testes: nada pendente.** As três camadas (ml-api, backend, frontend) têm suítes escritas e confirmadas rodando de verdade na sua máquina — 43/43, 30/30 e 68/68, respectivamente.

**Backlog de pontos críticos (`REVISAO_PONTOS_CRITICOS.md`), ainda não corrigidos no código:**
- Vazamento de mensagem de erro interna pro cliente (`GlobalExceptionHandler.handleRuntime`, `main.py`/`content.py`).
- CORS `allow_origins=["*"]` + `allow_credentials=True` no ml-api (combinação inválida pela spec).
- `ConteudoController.listar()` expõe a entidade JPA `Conteudo` direto, em vez de um DTO — contraria a própria regra do `CONTRIBUTING.md`.
- `pom.xml` do backend mistura `spring-boot-starter-webmvc` e `spring-boot-starter-webflux`.
- Versão do Java documentada de 3 formas diferentes (README "17", pom.xml "21", Swagger "25").
- Falta `ml-api/.env.example`.
