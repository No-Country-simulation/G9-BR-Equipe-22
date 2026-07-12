# 🤖 ML API — TechTagger

API interna em FastAPI que carrega o modelo treinado e serve classificação, extração de palavras-chave e busca semântica.

## Como rodar

```bash
cd ml-api
pip install -r requirements.txt
uvicorn app.main:app --reload
```

- Swagger: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/health` | Status da API + se o modelo carregou |
| GET | `/stats` | Métricas do modelo |
| POST | `/content` | Processa um conteúdo (principal) |
| POST | `/content/batch` | Processa múltiplos conteúdos |
| POST | `/content/search` | Busca semântica |
| GET | `/content/categories` | Lista categorias suportadas |

## Estrutura

```
ml-api/
├── app/
│   ├── main.py             # Entry point
│   ├── config.py           # Configurações (via .env)
│   ├── schemas.py          # Modelos Pydantic (request/response)
│   ├── services/
│   │   ├── ml_service.py       # Classificação, keywords, recomendação
│   │   └── storage_service.py  # Carrega modelo (local ou OCI)
│   ├── routes/
│   │   ├── content.py
│   │   └── stats.py
│   └── utils/
│       └── text_utils.py
├── models/                 # Arquivos do modelo treinado
├── tests/
└── requirements.txt
```

## Variáveis de ambiente

Copie `.env.example` para `.env` e ajuste conforme necessário:

```env
APP_NAME=TechTagger
MODELS_DIR=./models
USE_OCI_STORAGE=False
OCI_NAMESPACE=seu-namespace
OCI_BUCKET_NAME=techtagger-models
```

## Rodar testes

```bash
pytest tests/ -v
```

## Docker

```bash
docker build -t techtagger-ml-api .
docker run -p 8000:8000 techtagger-ml-api
```
