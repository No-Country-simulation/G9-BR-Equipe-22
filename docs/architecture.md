# Arquitetura — TechTagger

## Visão geral

O TechTagger é dividido em três camadas independentes que se comunicam via HTTP:

```
Cliente → Backend (Java) → ML API (Python) → Modelo treinado
                ↓
             MySQL
```

## Por que essa separação?

- **ML API isolada**: o time de dados evolui o modelo sem depender do código Java, e vice-versa
- **Backend como fachada pública**: centraliza validação, persistência e regras de negócio, seguindo o que o edital do hackathon exige (API REST que consome o modelo de Ciência de Dados)
- **Banco de dados separado**: permite consultar histórico de conteúdos processados sem re-chamar o modelo toda vez

## Fluxo detalhado de uma requisição

1. Cliente envia `POST /conteudo` para o Backend com `{ "titulo": ..., "texto": ... }`
2. Backend valida os campos (`@Valid`)
3. Backend chama `POST /content` na ML API via `WebClient`
4. ML API:
   - Limpa o texto
   - Classifica com o pipeline TF-IDF + Regressão Logística
   - Extrai palavras-chave com YAKE
   - Busca conteúdos relacionados com embeddings (Sentence Transformers + similaridade de cosseno)
   - Devolve tudo em JSON
5. Backend recebe a resposta, persiste no MySQL, e devolve ao cliente

## Decisões técnicas

| Decisão | Motivo |
|---|---|
| MySQL em vez de banco em memória | Persistência real, requisito do edital, e integra bem com OCI MySQL HeatWave |
| TF-IDF + Regressão Logística | Simples, rápido de treinar, interpretável, funciona bem com poucos dados |
| Sentence Transformers para busca | Captura similaridade semântica além de palavras exatas |
| Monorepo | Facilita avaliação (um clone só) e histórico de commits unificado |
