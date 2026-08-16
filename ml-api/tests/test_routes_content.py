def test_post_content_processa_conteudo_valido(client, conteudo_valido):
    resp = client.post("/content", json=conteudo_valido)
    assert resp.status_code == 200
    body = resp.json()
    assert "category" in body
    assert "keywords" in body
    assert "related_content" in body
    assert "processed_at" in body


def test_post_content_rejeita_titulo_vazio(client):
    resp = client.post("/content", json={"title": "", "text": "Um texto com mais de dez caracteres."})
    assert resp.status_code == 422


def test_post_content_rejeita_texto_curto_demais(client, conteudo_valido):
    payload = {**conteudo_valido, "text": "curto"}
    resp = client.post("/content", json=payload)
    assert resp.status_code == 422


def test_post_content_rejeita_campos_ausentes(client):
    resp = client.post("/content", json={})
    assert resp.status_code == 422


def test_post_content_batch_processa_multiplos_itens(client, conteudo_valido):
    payload = {"items": [conteudo_valido, conteudo_valido]}
    resp = client.post("/content/batch", json=payload)
    assert resp.status_code == 200
    body = resp.json()
    assert body["total"] == 2
    assert len(body["results"]) == 2


def test_post_content_batch_rejeita_lista_vazia(client):
    resp = client.post("/content/batch", json={"items": []})
    assert resp.status_code == 422


def test_post_content_search_retorna_resultados(client):
    resp = client.post("/content/search", json={"query": "kubernetes deployment strategies", "top_n": 3})
    assert resp.status_code == 200
    body = resp.json()
    assert body["total"] == 3
    assert len(body["results"]) == 3


def test_get_content_categories_lista_categorias_suportadas(client):
    resp = client.get("/content/categories")
    assert resp.status_code == 200
    body = resp.json()
    assert body["total"] > 0
    assert isinstance(body["categories"], list)
