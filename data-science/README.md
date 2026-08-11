# 📊 Data Science — TechTagger

Notebook e dados usados para treinar o modelo de classificação e busca semântica.

## Estrutura

```
data-science/
├── notebooks/     # EDA, tratamento, treino e avaliação
├── data/
│   ├── raw/         # Dados brutos coletados
│   └── processed/   # Dados já limpos/tratados
└── src/            # Scripts auxiliares reaproveitáveis (opcional)
```

## Pipeline

1. **Coleta** — dados obtidos de fontes públicas, documentações e/ou produzidos pela equipe
2. **EDA** — exploração e limpeza de dados
3. **Tratamento de texto** — normalização, remoção de stopwords
4. **Vetorização** — TF-IDF (n-gramas 1-2)
5. **Treino** — Regressão Logística
6. **Avaliação** — acurácia, F1-score, matriz de confusão
7. **Serialização** — `joblib.dump()` do pipeline completo

## Saídas geradas

Ao final do notebook, exportar para `ml-api/models/`:
- `classifier.pkl` — pipeline TF-IDF + modelo treinado
- `embeddings.npy` — embeddings semânticos da base de conhecimento
- `metadata.csv` — título/categoria/url de cada item da base
- `model_info.json` — métricas e metadados do treino

## Como rodar

```bash
cd data-science
pip install -r requirements.txt
jupyter notebook notebooks/
```

## 🧠 Modelo de Machine Learning

- **Algoritmo:** TF‑IDF (n‑grams 1–3) + Regressão Logística, otimizado por **GridSearchCV**
- **Melhores hiperparâmetros:** `C=2.0`, `max_features=5000`, `ngram_range=(1,3)`
- **Embeddings (recomendação):** sentence‑transformers `all-MiniLM-L6-v2`
- **Keywords:** YAKE com fallback automático por TF‑IDF
- **Dataset:** 1.520 artigos técnicos balanceados (Dev.to API), 8 categorias
- **Protocolo de avaliação:** treino com tags como sinal auxiliar; **teste apenas com título + descrição** (sem vazamento)

### 📊 Métricas (teste honesto)
| Métrica | Valor |
|---|---|
| **Accuracy** | **0.955** |
| **F1 (weighted)** | 0.955 |
| **F1 (macro)** | 0.955 |
| **F1 (CV 5‑fold)** | 0.960 |

### 🏷️ Categorias (F1 por classe)
Mobile 0.99 · Cloud 0.99 · Data Science 0.97 · Frontend 0.96 · Backend 0.95 · Databases 0.94 · DevOps 0.93 · Data Engineering 0.91
