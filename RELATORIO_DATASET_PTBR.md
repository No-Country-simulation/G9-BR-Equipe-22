# Dataset traduzido para PT-BR + retreino do modelo

**Status: concluído e em produção.** Todas as etapas abaixo já foram confirmadas rodando — inclusive o passo que antes dependia da sua máquina (regeneração dos embeddings), que já foi executado e testado de ponta a ponta.

## 1. Tradução completa do dataset

`data-science/data/techtagger_dataset_expanded.csv` (1.520 linhas, 100% em inglês) foi traduzido para português, gerando `data-science/data/techtagger_dataset_ptbr.csv`. Termos técnicos e nomes de produtos/frameworks (TypeScript, AWS, React, Kubernetes, PostgreSQL etc.) foram mantidos em inglês — como é padrão em conteúdo técnico brasileiro — e o restante (estrutura da frase, explicações, benefícios) foi traduzido para português.

Boa parte dos títulos/textos do dataset segue um pequeno conjunto de templates gerados automaticamente (`"Getting Started with X"`, `"A Practical Guide to X"`, `"X: Y"` etc.). Isso permitiu extrair um glossário de ~460 termos técnicos únicos e ~235 frases de benefício únicas, traduzir cada um **uma única vez** e reconstruir 1.369 linhas de forma consistente e programática. As **151 linhas restantes**, que não seguiam nenhum template (títulos e textos escritos livremente), foram traduzidas uma a uma manualmente.

Resultado: **1.520/1.520 linhas traduzidas**, 0 valores nulos, distribuição de categorias idêntica à original (Cloud 191, Frontend/Backend/Data Science/Databases/Mobile/DevOps 190 cada, Data Engineering 189).

O dataset original em inglês (`techtagger_dataset_expanded.csv`) não foi apagado nem sobrescrito — continua no repositório, caso a equipe queira manter suporte a conteúdo em inglês no futuro ou usá-lo como referência.

## 2. Correção do bug de acentuação

`ml-api/app/utils/text_utils.py::limpar_texto` apagava qualquer letra acentuada (ex.: "ótimo" virava " timo") em vez de preservá-la. Isso era ainda mais crítico com o modelo treinado em português — se essa função continuasse quebrada, o texto em PT-BR chegaria mutilado ao classificador em produção mesmo com um dataset e modelo corretos.

A correção também resolveu um descompasso que já existia: o notebook de treino (`clean_text_v4`) já preservava acentos, mas a função usada em produção não — ou seja, o texto visto no treino era diferente do texto visto ao classificar requisições reais. O teste correspondente (`ml-api/tests/test_text_utils.py`) foi atualizado: antes documentava o bug, agora confirma que os acentos são preservados.

## 3. Retreino do classificador

TF-IDF + Regressão Logística com GridSearchCV (variando `max_features`, `ngram_range`, `min_df` e `C`), mesma abordagem documentada no `data-science/README.md`, usando o dataset traduzido e a função de limpeza já corrigida.

| Métrica | Modelo original (inglês) | Modelo novo (PT-BR) |
|---|---|---|
| Accuracy | 0,955 | **0,970** |
| F1 (weighted) | 0,955 | **0,970** |
| F1 (macro) | 0,955 | **0,970** |
| F1 (CV 5-fold) | 0,960 | **0,973** |
| Melhores hiperparâmetros | C=2.0, max_features=5000, ngram(1,3) | C=2.0, max_features=8000, ngram(1,2), min_df=1 |

A acurácia no teste em português (97%) ficou levemente acima da original em inglês (95,5%) — sinal de que a tradução manteve uma separação clara entre categorias, e que o dataset traduzido é adequado para treinar o classificador.

## 4. Embeddings regenerados e modelo em produção

Os artefatos novos (`classifier.pkl`, `metadata.csv`, `model_info.json`) foram copiados para `ml-api/models/`, e o `embeddings.npy` — usado na recomendação de conteúdos relacionados por similaridade semântica — foi regenerado a partir do `metadata.csv` novo com `regenerate_embeddings.py` (`SentenceTransformer("all-MiniLM-L6-v2")`), diretamente na sua máquina, já que o download desse modelo do Hugging Face fica bloqueado neste sandbox.

Os quatro arquivos em `ml-api/models/` estão consistentes e em português. Testado de ponta a ponta pela interface (não só por linha de comando): o exemplo "Introdução ao Spring Boot" foi processado corretamente, com a categoria certa e conteúdos relacionados todos da mesma categoria e boa similaridade — confirmando que classificador, metadata e embeddings estão sincronizados.

A pasta órfã `API de ml/`, que continha uma cópia antiga desses mesmos artefatos (em inglês) sem nenhuma referência no código, foi movida para `_to_delete/`.

## 5. Testes automatizados

A suíte da ml-api (`pytest tests/ -v --cov=app`) foi rodada por completo com o modelo novo: **43/43 testes passaram, 92% de cobertura** — incluindo os testes que dependem do modelo (`test_ml_service.py`, `test_routes_content.py`).
