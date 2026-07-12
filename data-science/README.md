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

## Métricas atuais do modelo

| Métrica | Valor |
|---|---|
| Amostras | 388 |
| Categorias | 8 |
| Acurácia (teste) | ~75,6% |
| F1-score ponderado (teste) | ~76,1% |
