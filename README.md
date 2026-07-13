# 🧠 TechTagger

> Classifique, rotule e organize conteúdo técnico automaticamente.

[![Backend CI](https://github.com/SEU_GRUPO/techtagger/actions/workflows/backend-ci.yml/badge.svg)](../../actions/workflows/backend-ci.yml)
[![ML API CI](https://github.com/SEU_GRUPO/techtagger/actions/workflows/ml-api-ci.yml/badge.svg)](../../actions/workflows/ml-api-ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)

Projeto desenvolvido para o Hackathon **TechMind** — Organização Inteligente de Conhecimento Técnico. Recebe um conteúdo técnico (título + texto) e devolve categoria, palavras-chave e conteúdos relacionados, tudo em JSON, pronto para consumo por outras aplicações.

---

## 📑 Índice

- [Sobre o projeto](#-sobre-o-projeto)
- [Arquitetura](#️-arquitetura)
- [Estrutura de pastas](#-estrutura-de-pastas)
- [Tecnologias](#️-tecnologias)
- [Como executar](#-como-executar-o-projeto-completo)
- [Exemplo de uso](#-exemplo-de-uso)
- [Sobre o modelo](#-sobre-o-modelo)
- [Integração com OCI](#️-integração-com-oci)
- [Equipe](#-equipe)
- [Contribuindo](#-contribuindo)

---

## 📌 Sobre o projeto

Profissionais e estudantes de tecnologia consomem diariamente grandes volumes de conteúdo técnico (artigos, documentação, cursos), o que torna difícil organizar, localizar e reutilizar essa informação depois. O TechTagger automatiza essa organização usando Ciência de Dados.

**Funcionalidades principais:**
- ✅ Classificação automática de conteúdo por categoria técnica
- ✅ Extração de palavras-chave relevantes
- ✅ Recomendação de conteúdos relacionados por similaridade semântica
- ✅ API REST documentada, com validação de entrada e tratamento de erros
- ✅ Persistência dos conteúdos processados em banco de dados
- ✅ Integração com OCI para armazenamento e hospedagem

---

## 🏗️ Arquitetura

```
                        ┌───────────────────────┐
                        │   Cliente / Front-end │
                        └──────────┬────────────┘
                                   │ HTTP
                                   ▼
                        ┌───────────────────────┐
                        │   Backend (Java)      │
                        │   Spring Boot         │
                        │   API pública         │
                        │   :8080               │
                        └──────────┬────────────┘
                          │                  │
                chama via HTTP        persiste
                          ▼                  ▼
              ┌─────────────────────┐ ┌─────────────────┐
              │  ML API (Python)    │ │  MySQL          │
              │  FastAPI            │ │  :3306          │
              │  Modelo TF-IDF +    │ └─────────────────┘
              │  Regressão Logística│
              │  :8000              │
              └─────────────────────┘
```

**Fluxo de uma requisição:**
1. Cliente envia `título + texto` para o Backend (`POST /conteudo`)
2. Backend valida a entrada e chama a ML API internamente
3. ML API classifica o texto, extrai palavras-chave e busca conteúdos relacionados
4. Backend recebe o resultado, salva no MySQL, e devolve a resposta ao cliente

---

## 📁 Estrutura de pastas

```
techtagger/
│
├── .github/                    # Templates de Issue/PR e workflows de CI
│   ├── ISSUE_TEMPLATE/
│   ├── workflows/
│   └── PULL_REQUEST_TEMPLATE.md
│
├── data-science/                # Ciência de Dados
│   ├── notebooks/                 # Jupyter/Colab (EDA, treino, avaliação)
│   ├── data/
│   │   ├── raw/                     # Dados brutos
│   │   └── processed/               # Dados tratados
│   ├── src/                       # Scripts auxiliares (opcional)
│   └── README.md
│
├── ml-api/                      # API Python que serve o modelo
│   ├── app/
│   │   ├── main.py                  # Entry point FastAPI
│   │   ├── config.py                # Configurações
│   │   ├── schemas.py               # Modelos Pydantic
│   │   ├── services/                # Lógica de ML e storage
│   │   ├── routes/                  # Endpoints
│   │   └── utils/                   # Funções auxiliares
│   ├── models/                    # Modelo treinado (.pkl, .npy, .csv, .json)
│   ├── tests/
│   ├── requirements.txt
│   ├── Dockerfile
│   └── README.md
│
├── backend/                     # API Java pública
│   ├── src/main/java/com/techtagger/backend/
│   │   ├── controller/               # Endpoints REST
│   │   ├── service/                  # Regras de negócio
│   │   ├── client/                   # Cliente HTTP para a ML API
│   │   ├── dto/                      # Objetos de request/response
│   │   ├── model/                    # Entidades JPA
│   │   ├── repository/               # Acesso a dados
│   │   ├── config/                   # Configurações (WebClient, etc.)
│   │   └── exception/                # Tratamento de erros
│   ├── src/main/resources/
│   │   └── db/migration/             # Migrations Flyway
│   ├── src/test/
│   ├── pom.xml
│   ├── Dockerfile
│   └── README.md
│
├── frontend/                    # Interface web (opcional)
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/                 # Chamadas à API
│   │   └── assets/
│   ├── public/
│   └── README.md
│
├── infra/                       # Infraestrutura
│   ├── docker-compose.yml         # Sobe MySQL + Adminer localmente
│   ├── oci/                       # Scripts/configs de deploy OCI
│   └── README.md
│
├── docs/                         # Documentação complementar
│   ├── architecture.md
│   ├── api-examples.md
│   └── images/
│
├── .gitignore
├── LICENSE
├── README.md                    # Este arquivo
└── CONTRIBUTING.md              # Guia de contribuição
```

---

## 🛠️ Tecnologias

| Camada | Tecnologias |
|---|---|
| Ciência de Dados | Python, Pandas, Scikit-Learn, TF-IDF, Sentence Transformers, YAKE |
| ML API | FastAPI, Uvicorn, Pydantic |
| Backend | Java 17, Spring Boot, Spring Data JPA, WebClient, Flyway |
| Frontend (opcional) | React / Vue *(definir conforme o time)* |
| Banco de Dados | MySQL 8 |
| Infraestrutura | Docker, Docker Compose, OCI (Object Storage, Compute) |
| CI/CD | GitHub Actions |

---

## 🚀 Como executar o projeto completo

### Pré-requisitos
- [Docker](https://www.docker.com/) e Docker Compose
- [Java 17+](https://adoptium.net/)
- [Python 3.11+](https://www.python.org/)
- Maven (ou use o `./mvnw` incluso no backend)

### 1. Clonar o repositório
```bash
git clone https://github.com/No-Country-simulation/G9-BR-Equipe-22.git
cd G9-BR-Equipe-22
```

### 2. Subir o banco de dados
```bash
cd infra
docker compose up -d
cd ..
```

### 3. Subir a ML API (Python)
```bash
cd ml-api
pip install -r requirements.txt
uvicorn app.main:app --reload
```
➡️ Disponível em `http://localhost:8000` — documentação interativa em `/docs`.

### 4. Subir o Backend (Java)
Em outro terminal:
```bash
cd backend
./mvnw spring-boot:run
```
➡️ Disponível em `http://localhost:8080`.

### 5. (Opcional) Subir o Frontend
```bash
cd frontend
npm install
npm run dev
```

---

## 📡 Exemplo de uso

**Requisição:**
```bash
curl -X POST http://localhost:8080/conteudo \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Introdução ao Spring Boot",
    "texto": "Neste conteúdo são apresentados os conceitos básicos para criação de APIs REST utilizando Java e Spring Boot."
  }'
```

**Resposta:**
```json
{
  "categoria": "Backend",
  "probabilidade": 0.91,
  "keywords": ["Spring Boot", "Java", "REST API"],
  "relacionados": [
    {
      "title": "Building REST APIs with Spring",
      "category": "Backend",
      "similarity": 0.87
    }
  ]
}
```

Mais exemplos em [`docs/api-examples.md`](./docs/api-examples.md), [`backend/README.md`](./backend/README.md) e [`ml-api/README.md`](./ml-api/README.md).

---

## 📊 Sobre o modelo

| Métrica | Valor |
|---|---|
| Categorias | Backend, Cloud, Data Engineering, Data Science, Databases, DevOps, Frontend, Mobile |
| Amostras de treino | 388 |
| Acurácia (teste) | ~75,6% |
| F1-score ponderado (teste) | ~76,1% |
| Abordagem de classificação | TF-IDF + Regressão Logística |
| Abordagem de busca semântica | Sentence Transformers (`all-MiniLM-L6-v2`) |

Detalhes completos do treino, EDA e avaliação em [`data-science/notebooks/`](./data-science/notebooks/).

---

## ☁️ Integração com OCI

| Serviço OCI | Uso no projeto |
|---|---|
| **Object Storage** | Armazenamento dos arquivos do modelo (`.pkl`, `.npy`, `.csv`, `.json`) |
| **OCI Compute** | Hospedagem da ML API e/ou do Backend |
| **Banco de dados** *(opcional)* | Alternativa gerenciada ao MySQL local |

Detalhes de configuração em [`infra/README.md`](./infra/README.md).

---

## 👥 Equipe

| Nome | Papel | GitHub |
|---|---|---|
| _(preencher)_ | Ciência de Dados | |
| _(preencher)_ | Backend | |
| _(preencher)_ | Frontend | |
| _(preencher)_ | DevOps / OCI | |

---

## 🤝 Contribuindo

Veja o guia completo em [CONTRIBUTING.md](./CONTRIBUTING.md) — fluxo de branches, padrão de commits, como abrir Pull Requests e mais.

---

## 📄 Licença

Distribuído sob a licença MIT. Veja [LICENSE](./LICENSE) para mais detalhes.

Projeto desenvolvido para fins educacionais no Hackathon TechMind (No Country).
