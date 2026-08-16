def test_health_retorna_ok_e_modelo_carregado(client):
    resp = client.get("/health")
    assert resp.status_code == 200
    body = resp.json()
    assert body["status"] == "ok"
    assert body["model_loaded"] is True


def test_stats_retorna_metricas_do_modelo(client):
    resp = client.get("/stats")
    assert resp.status_code == 200
    body = resp.json()
    assert body["total_content"] > 0
    assert body["n_categories"] > 0
    assert isinstance(body["categories"], list)
