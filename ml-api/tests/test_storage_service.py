"""
Testes para StorageService isolados do modelo real: usam um diretório
temporário no lugar de `models/`, então não dependem dos arquivos .pkl/.npy
treinados nem baixam nada da rede.
"""
import json

import numpy as np
import pandas as pd
import pytest

from app.services.storage_service import StorageService


@pytest.fixture
def storage(tmp_path):
    svc = StorageService()
    svc.models_dir = str(tmp_path)
    return svc


def test_carregar_metadata_le_csv_existente(storage, tmp_path):
    pd.DataFrame({"title": ["a"], "category": ["Backend"]}).to_csv(
        tmp_path / "metadata.csv", index=False
    )
    df = storage.carregar_metadata()
    assert list(df["title"]) == ["a"]


def test_carregar_embeddings_le_npy_existente(storage, tmp_path):
    np.save(tmp_path / "embeddings.npy", np.array([[1.0, 2.0], [3.0, 4.0]]))
    emb = storage.carregar_embeddings()
    assert emb.shape == (2, 2)


def test_carregar_info_le_json_existente(storage, tmp_path):
    (tmp_path / "model_info.json").write_text(json.dumps({"version": "9.9.9"}))
    info = storage.carregar_info()
    assert info["version"] == "9.9.9"


def test_arquivo_ausente_sem_oci_levanta_file_not_found(storage):
    with pytest.raises(FileNotFoundError):
        storage.carregar_metadata()


def test_arquivo_ausente_com_oci_habilitado_tenta_baixar(storage, monkeypatch, tmp_path):
    """
    Com USE_OCI_STORAGE=True e arquivo ausente localmente, o serviço deve
    tentar baixar do OCI em vez de falhar imediatamente. Aqui simulamos o
    download (sem chamar a API real da OCI) para garantir que o método
    correto é acionado e que o resultado passa a existir no disco.
    """
    from app.services import storage_service as storage_service_module

    monkeypatch.setattr(storage_service_module.settings, "use_oci_storage", True)

    chamadas = []

    def fake_baixar(self, object_name, destino):
        chamadas.append((object_name, destino))
        pd.DataFrame({"title": ["baixado"], "category": ["Cloud"]}).to_csv(destino, index=False)

    monkeypatch.setattr(StorageService, "_baixar_do_oci", fake_baixar)

    df = storage.carregar_metadata()
    assert chamadas == [("metadata.csv", str(tmp_path / "metadata.csv"))]
    assert list(df["title"]) == ["baixado"]
