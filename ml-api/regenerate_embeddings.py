"""
Regera embeddings.npy a partir do metadata.csv já traduzido para PT-BR.

Este script NÃO pôde ser executado no sandbox de revisão porque baixar o
modelo `all-MiniLM-L6-v2` do Hugging Face está bloqueado pela rede desse
ambiente. Rode este script na sua máquina (ou em qualquer ambiente com
acesso normal à internet) depois de colocar os novos classifier.pkl e
metadata.csv (gerados nesta revisão) em ml-api/models/.

Uso:
    cd ml-api
    python regenerate_embeddings.py
"""
import numpy as np
import pandas as pd
from sentence_transformers import SentenceTransformer

print("Carregando metadata.csv (PT-BR)...")
metadata = pd.read_csv("models/metadata.csv")

print("Carregando modelo de embeddings all-MiniLM-L6-v2 (baixa da internet na 1a vez)...")
model = SentenceTransformer("all-MiniLM-L6-v2")

print(f"Gerando embeddings para {len(metadata)} artigos...")
embeddings = model.encode(
    metadata["content_clean"].tolist(),
    show_progress_bar=True,
    batch_size=32,
)

np.save("models/embeddings.npy", embeddings)
print(f"embeddings.npy salvo! Shape: {embeddings.shape}")
print("Pronto — classifier.pkl, metadata.csv e embeddings.npy agora estão todos alinhados em PT-BR.")
