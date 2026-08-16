"""Testes unitários para os schemas Pydantic (app.schemas).

Não exigem carregar o modelo de ML — rodam em milissegundos e travam
regressões de validação de entrada (ex.: título vazio, texto curto demais).
"""
import pytest
from pydantic import ValidationError

from app.schemas import ContentRequest, BatchContentRequest, SearchRequest


def test_content_request_aceita_dados_validos():
    req = ContentRequest(title="Titulo valido", text="Um texto com mais de dez caracteres.")
    assert req.title == "Titulo valido"


def test_content_request_rejeita_titulo_curto_demais():
    with pytest.raises(ValidationError):
        ContentRequest(title="ab", text="Um texto com mais de dez caracteres.")


def test_content_request_rejeita_texto_curto_demais():
    with pytest.raises(ValidationError):
        ContentRequest(title="Titulo valido", text="curto")


def test_content_request_rejeita_titulo_somente_espacos():
    with pytest.raises(ValidationError):
        ContentRequest(title="     ", text="Um texto com mais de dez caracteres.")


def test_content_request_faz_strip_no_titulo_e_texto():
    req = ContentRequest(title="  Titulo  ", text="  Um texto com mais de dez caracteres.  ")
    assert req.title == "Titulo"
    assert req.text == "Um texto com mais de dez caracteres."


def test_batch_content_request_rejeita_lista_vazia():
    with pytest.raises(ValidationError):
        BatchContentRequest(items=[])


def test_batch_content_request_rejeita_mais_de_100_itens():
    item = {"title": "Titulo valido", "text": "Um texto com mais de dez caracteres."}
    with pytest.raises(ValidationError):
        BatchContentRequest(items=[item] * 101)


def test_search_request_usa_top_n_padrao_5():
    req = SearchRequest(query="busca valida")
    assert req.top_n == 5


def test_search_request_rejeita_top_n_fora_do_intervalo():
    with pytest.raises(ValidationError):
        SearchRequest(query="busca valida", top_n=0)
    with pytest.raises(ValidationError):
        SearchRequest(query="busca valida", top_n=21)
