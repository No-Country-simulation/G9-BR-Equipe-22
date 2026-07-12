# ☕ Backend — TechTagger

API pública em Spring Boot. Recebe requisições do cliente, consome a ML API internamente, e persiste os resultados no MySQL.

## Como rodar

Pré-requisito: banco de dados rodando (veja [`infra/`](../infra)) e ML API rodando (veja [`ml-api/`](../ml-api)).

```bash
cd backend
./mvnw spring-boot:run
```

Disponível em `http://localhost:8080`.

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/conteudo` | Processa um novo conteúdo |
| GET | `/conteudo/{id}` | Busca um conteúdo processado |
| GET | `/conteudo?categoria=Backend` | Filtra por categoria |

## Estrutura

```
backend/
├── src/main/java/com/techtagger/backend/
│   ├── controller/    # Endpoints REST
│   ├── service/       # Regras de negócio
│   ├── client/        # Chamada HTTP para a ML API
│   ├── dto/            # Objetos de request/response
│   ├── model/          # Entidades JPA
│   ├── repository/     # Acesso a dados
│   ├── config/          # WebClient, CORS, etc.
│   └── exception/       # Tratamento global de erros
└── src/main/resources/
    ├── application.properties
    └── db/migration/    # Migrations Flyway
```

## Configuração

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/techtagger
spring.datasource.username=techtagger_user
spring.datasource.password=techtagger_pass

spring.flyway.enabled=true

ml.service.url=http://localhost:8000
```

## Rodar testes

```bash
./mvnw test
```

## Docker

```bash
docker build -t techtagger-backend .
docker run -p 8080:8080 techtagger-backend
```
