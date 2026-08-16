import re


def limpar_texto(texto: str) -> str:
    """Limpeza de texto técnico, com suporte a português (PT-BR) e inglês.

    Corrige um bug encontrado na versão anterior: a regex antiga só mantinha
    caracteres a-z0-9, o que APAGAVA letras acentuadas (ótimo -> " timo") em
    vez de preservá-las. Isso também gerava um descompasso entre o
    pré-processamento usado no treino do modelo (que já preservava acentos,
    em clean_text_v4 no notebook) e o pré-processamento usado em produção
    nesta função — ou seja, o texto que o modelo via ao treinar era
    diferente do texto que via ao classificar requisições reais. Esta versão
    usa a mesma lógica em treino e produção.
    """
    if not isinstance(texto, str):
        return ""
    texto = texto.lower()
    texto = re.sub(r'http\S+|www\.\S+', '', texto)
    texto = re.sub(r'[^a-z0-9áéíóúâêîôûãõàèìòùäëïöüç\s\.\-\+#]', ' ', texto)
    texto = re.sub(r'\s+', ' ', texto).strip()
    return texto
