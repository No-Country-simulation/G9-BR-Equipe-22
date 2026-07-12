import numpy as np
import yake
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity
from app.services.storage_service import StorageService
from app.utils.text_utils import limpar_texto


class MLService:
    """Serviço central de Machine Learning."""

    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance

    def __init__(self):
        if self._initialized:
            return

        print("Inicializando MLService...")
        storage = StorageService()

        self.classifier = storage.carregar_classificador()
        self.embeddings = storage.carregar_embeddings()
        self.metadata = storage.carregar_metadata()
        self.info = storage.carregar_info()

        print("Carregando SentenceTransformer...")
        self.embedder = SentenceTransformer("all-MiniLM-L6-v2")

        self.kw_extractor = yake.KeywordExtractor(
            lan="en", n=2, dedupLim=0.7, top=5
        )

        self._initialized = True
        print("MLService pronto!")

    def classificar(self, texto: str, top_n: int = 3):
        texto_limpo = limpar_texto(texto)
        probs = self.classifier.predict_proba([texto_limpo])[0]
        classes = self.classifier.classes_
        top_idx = np.argsort(probs)[::-1][:top_n]

        return {
            "category": str(classes[top_idx[0]]),
            "probability": round(float(probs[top_idx[0]]), 4),
            "top_categories": [
                {"name": str(classes[i]), "score": round(float(probs[i]), 4)}
                for i in top_idx
            ]
        }

    def extrair_keywords(self, texto: str, top: int = 5):
        keywords = self.kw_extractor.extract_keywords(texto)
        return [kw for kw, score in keywords[:top]]

    def recomendar(self, texto: str, top_n: int = 5):
        texto_limpo = limpar_texto(texto)
        emb_query = self.embedder.encode([texto_limpo])
        sims = cosine_similarity(emb_query, self.embeddings)[0]
        top_idx = np.argsort(sims)[::-1][:top_n]

        return [
            {
                "title": str(self.metadata.iloc[i]["title"]),
                "category": str(self.metadata.iloc[i]["category"]),
                "similarity": round(float(sims[i]), 4),
                "url": self._url_ou_none(self.metadata.iloc[i].get("url"))
            }
            for i in top_idx
        ]

    @staticmethod
    def _url_ou_none(valor):
        """Converte NaN/vazio pra None de verdade (em vez da string 'nan')."""
        if valor is None or (isinstance(valor, float) and np.isnan(valor)):
            return None
        valor_str = str(valor).strip()
        return valor_str if valor_str else None

    def processar_completo(self, title: str, text: str):
        texto_completo = f"{title}. {text}"

        classificacao = self.classificar(texto_completo, top_n=3)
        keywords = self.extrair_keywords(texto_completo, top=5)
        related = self.recomendar(texto_completo, top_n=3)

        return {
            **classificacao,
            "keywords": keywords,
            "related_content": related
        }

    def buscar(self, query: str, top_n: int = 5):
        return self.recomendar(query, top_n=top_n)

    def get_stats(self):
        return {
            "total_content": len(self.metadata),
            "n_categories": self.info.get("n_categories", 0),
            "categories": self.info.get("categories", []),
            "model_version": self.info.get("model_version", "1.0.0"),
            "accuracy": self.info.get("accuracy_test", 0.0),
            "f1_score": self.info.get("f1_weighted_test", 0.0),
            "trained_at": self.info.get("trained_at", "")
        }
