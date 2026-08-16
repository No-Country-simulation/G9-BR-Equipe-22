"""
Fixtures compartilhadas para os testes da ml-api.

Observação: os testes que usam `client`/`app` carregam o MLService real
(classificador + embeddings treinados em app/../models). Isso é intencional:
como o time ainda não tem nenhum teste automatizado, o valor imediato maior
vem de testes de integração que garantem que a API sobe e responde
corretamente de ponta a ponta. Testes puramente unitários (sem I/O) ficam
em test_text_utils.py e test_schemas.py.
"""
import pytest
from fastapi.testclient import TestClient


@pytest.fixture(scope="session")
def client():
    from app.main import app
    return TestClient(app)


@pytest.fixture
def conteudo_valido():
    return {
        "title": "Introducao ao Spring Boot",
        "text": "Neste conteudo sao apresentados os conceitos basicos para "
                 "criacao de APIs REST utilizando Java e Spring Boot.",
    }
