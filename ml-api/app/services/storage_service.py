import os
import joblib
import numpy as np
import pandas as pd
import json
from app.config import get_settings

settings = get_settings()


class StorageService:
    """Carrega arquivos do modelo (local ou OCI Object Storage)."""

    def __init__(self):
        self.models_dir = settings.models_dir

    def _baixar_do_oci(self, object_name: str, destino: str):
        try:
            import oci
            config = oci.config.from_file(settings.oci_config_file)
            client = oci.object_storage.ObjectStorageClient(config)

            response = client.get_object(
                settings.oci_namespace,
                settings.oci_bucket_name,
                object_name
            )
            with open(destino, "wb") as f:
                for chunk in response.data.raw.stream(1024 * 1024, decode_content=False):
                    f.write(chunk)

            print(f"Baixado do OCI: {object_name}")
        except Exception as e:
            print(f"Erro ao baixar do OCI ({object_name}): {e}")
            raise

    def _garantir_arquivo(self, filename: str):
        caminho = os.path.join(self.models_dir, filename)

        if not os.path.exists(caminho):
            if settings.use_oci_storage:
                os.makedirs(self.models_dir, exist_ok=True)
                self._baixar_do_oci(filename, caminho)
            else:
                raise FileNotFoundError(f"Arquivo não encontrado: {caminho}")

        return caminho

    def carregar_classificador(self):
        caminho = self._garantir_arquivo("classifier.pkl")
        return joblib.load(caminho)

    def carregar_embeddings(self):
        caminho = self._garantir_arquivo("embeddings.npy")
        return np.load(caminho)

    def carregar_metadata(self):
        caminho = self._garantir_arquivo("metadata.csv")
        return pd.read_csv(caminho)

    def carregar_info(self):
        caminho = self._garantir_arquivo("model_info.json")
        with open(caminho, "r") as f:
            return json.load(f)
