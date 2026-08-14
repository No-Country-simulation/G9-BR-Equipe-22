# ☕ Backend — TechTagger
 
API pública em Spring Boot. Recebe requisições do cliente, valida os dados, consome a ML API internamente, e persiste os resultados no MySQL.
 
## Como rodar
 
Pré-requisito: banco de dados rodando (veja [`infra/`](../infra)) e ML API rodando (veja [`ml-api/`](../ml-api)).
 
```bash
cd backend
./mvnw spring-boot:run
```
 
Disponível em `http://localhost:8080`. Documentação interativa (Swagger) em `http://localhost:8080/swagger-ui/index.html`.
 
## Endpoints
 
| Método | Rota | Descrição |
|---|---|---|
| POST | `/conteudo` | Processa um novo conteúdo |
| GET | `/conteudo/{id}` | Busca um conteúdo processado |
| GET | `/conteudo?categoria=Backend&page=0&size=10` | Lista paginada, com filtro opcional por categoria |
| GET | `/conteudo/categorias` | Lista as categorias suportadas pelo modelo |
| POST | `/conteudo/batch` | Processa múltiplos conteúdos de uma vez (JSON, até 100 itens) |
| POST | `/conteudo/batch/csv` | Processa múltiplos conteúdos a partir de um arquivo CSV (`multipart/form-data`, campo `file`) |
 
**Resposta padrão** (`ConteudoResponse`):
```json
{
  "id": 1,
  "titulo": "Introdução ao Spring Boot",
  "texto": "Neste conteúdo são apresentados os conceitos básicos...",
  "categoria": "Backend",
  "probabilidade": 0.91,
  "keywords": ["Spring Boot", "Java", "REST API"],
  "relacionados": [{ "title": "...", "category": "Backend", "similarity": 0.87, "url": null }]
}
```
 
**CSV esperado** (para `/conteudo/batch/csv`): colunas `titulo` e `texto`, separadas por vírgula, com aspas em campos que contenham vírgula.
 
## Tratamento de erros
 
| Situação | Status |
|---|---|
| Validação (campo fora do tamanho permitido) | 400 |
| Conteúdo não encontrado | 404 |
| ML API indisponível, com erro ou em timeout | 503 |
| Erro inesperado | 500 |
 
Todas as respostas de erro seguem o formato `{ "error": "...", "detail": "..." }`.
 
## Estrutura
 
```
backend/
├── src/main/java/com/techtagger/backend/
│   ├── controller/    # Endpoints REST
│   ├── service/       # Regras de negócio
│   ├── client/        # Chamada HTTP para a ML API (com timeout e retry)
│   ├── dto/            # Objetos de request/response (inclui dto/ml, para o contrato com a ML API)
│   ├── model/          # Entidades JPA
│   ├── repository/     # Acesso a dados
│   ├── util/            # Utilitários (parser de CSV)
│   ├── config/           # WebClient, CORS, OpenAPI/Swagger
│   └── exception/        # Tratamento global de erros
└── src/main/resources/
    ├── application.properties
    └── db/migration/    # Migrations Flyway
```
 
## Configuração
 
`src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/techtagger?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo
spring.datasource.username=techtagger_user
spring.datasource.password=${DB_PASSWORD:techtagger_pass}
spring.flyway.enabled=true
ml.service.url=${ML_SERVICE_URL:http://localhost:8000}
```
 
`DB_PASSWORD` e `ML_SERVICE_URL` podem ser sobrescritos por variável de ambiente (útil em produção); os valores acima são os padrões de desenvolvimento local.
 
**Atenção à porta do MySQL:** o `docker-compose.yml` em `infra/` mapeia o MySQL para a porta `3307` (não a `3306` padrão), para evitar conflito com uma instância local já instalada.
