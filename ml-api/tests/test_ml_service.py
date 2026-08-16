"""
Testes de integração leve para MLService, usando o modelo real treinado
(app/../models/*.pkl, *.npy, *.csv, *.json) — os mesmos artefatos que a API
usa em produção. Isso garante que o pipeline completo (limpeza -> TF-IDF ->
classificação / embeddings -> similaridade) continua funcionando após
qualquer alteração no código ou no modelo.
"""
import math

import pytest

from app.services.ml_service import MLService

CATEGORIAS_ESPERADAS = {
    "Backend", "Cloud", "Data Engineering", "Data Science",
    "Databases", "DevOps", "Frontend", "Mobile",
}


@pytest.fixture(scope="module")
def ml():
    return MLService()


def test_ml_service_e_singleton():
    assert MLService() is MLService()


def test_classificar_retorna_categoria_conhecida(ml):
    resultado = ml.classificar(
        "React hooks tutorial for building reusable UI components with useState and useEffect"
    )
    assert resultado["category"] in CATEGORIAS_ESPERADAS
    assert 0.0 <= resultado["probability"] <= 1.0
    assert len(resultado["top_categories"]) == 3


def test_classificar_top_categories_ordenado_por_score_decrescente(ml):
    resultado = ml.classificar("Deploying containers to Kubernetes on AWS with Terraform")
    scores = [c["score"] for c in resultado["top_categories"]]
    assert scores == sorted(scores, reverse=True)
    assert resultado["probability"] == scores[0]


def test_extrair_keywords_retorna_lista_nao_vazia(ml):
    keywords = ml.extrair_keywords(
        "Introduction to relational databases, indexing strategies and query optimization in PostgreSQL",
        top=5,
    )
    assert isinstance(keywords, list)
    assert len(keywords) <= 5


def test_recomendar_retorna_itens_ordenados_por_similaridade(ml):
    resultados = ml.recomendar("Building REST APIs with Java and Spring Boot", top_n=5)
    assert len(resultados) == 5
    sims = [r["similarity"] for r in resultados]
    assert sims == sorted(sims, reverse=True)
    for r in resultados:
        assert set(r.keys()) == {"title", "category", "similarity", "url"}


def test_processar_completo_agrega_classificacao_keywords_e_relacionados(ml):
    resultado = ml.processar_completo(
        "Getting started with Docker",
        "A practical introduction to containers, images and docker-compose for local development.",
    )
    assert "category" in resultado
    assert "keywords" in resultado
    assert "related_content" in resultado
    assert len(resultado["related_content"]) == 3


def test_get_stats_retorna_metadados_do_modelo(ml):
    stats = ml.get_stats()
    assert stats["total_content"] > 0
    assert stats["n_categories"] == len(CATEGORIAS_ESPERADAS)
    assert set(stats["categories"]) == CATEGORIAS_ESPERADAS


@pytest.mark.parametrize("valor,esperado", [
    (None, None),
    (float("nan"), None),
    ("", None),
    ("   ", None),
    ("https://exemplo.com/artigo", "https://exemplo.com/artigo"),
])
def test_url_ou_none_normaliza_valores_vazios_e_nan(valor, esperado):
    if isinstance(valor, float) and math.isnan(valor):
        assert MLService._url_ou_none(valor) is None
    else:
        assert MLService._url_ou_none(valor) == esperado
