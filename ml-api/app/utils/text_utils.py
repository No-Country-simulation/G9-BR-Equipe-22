import re


def limpar_texto(texto: str) -> str:
    """Limpeza básica de texto técnico."""
    if not isinstance(texto, str):
        return ""
    texto = texto.lower()
    texto = re.sub(r'http\S+|www\.\S+', '', texto)
    texto = re.sub(r'[^a-z0-9\s\.\-\+#]', ' ', texto)
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto
