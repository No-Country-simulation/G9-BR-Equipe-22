"""Testes unitários para app.utils.text_utils.limpar_texto.

Nesta revisão, `limpar_texto` foi corrigida para PRESERVAR caracteres
acentuados em vez de descartá-los — antes, "ótimo" virava " timo" em vez de
manter o acento, o que distorcia qualquer texto em português antes de
chegar ao classificador (e também causava um descompasso entre o
pré-processamento usado no treino do modelo e o usado em produção). Estes
testes cobrem o comportamento corrigido.
"""
import pytest

from app.utils.text_utils import limpar_texto


def test_deixa_o_texto_em_minusculas():
    assert "python" in limpar_texto("Python is great")
    assert "PYTHON" not in limpar_texto("Python is great")


def test_remove_urls():
    texto = limpar_texto("veja mais em http://exemplo.com/pagina e www.outro.com")
    assert "http" not in texto
    assert "www" not in texto


def test_normaliza_espacos_em_branco():
    texto = limpar_texto("muitos    espacos\n\naqui")
    assert "  " not in texto
    assert texto == texto.strip()


def test_mantem_pontuacao_tecnica_relevante():
    texto = limpar_texto("Node.js e C++ com C#")
    assert "node.js" in texto
    assert "c++" in texto
    assert "c#" in texto


def test_entrada_nao_string_retorna_vazio():
    assert limpar_texto(None) == ""
    assert limpar_texto(123) == ""
    assert limpar_texto([]) == ""


def test_texto_vazio_retorna_vazio():
    assert limpar_texto("") == ""
    assert limpar_texto("   ") == ""


def test_caracteres_acentuados_sao_preservados():
    """
    Antes desta correção, a regex `[^a-z0-9\\s\\.\\-\\+#]` descartava qualquer
    caractere fora de a-z0-9, incluindo á/ã/ç/é etc. — "ótimo" virava " timo"
    em vez de manter o acento. Agora á/é/í/ó/ú/â/ê/î/ô/û/ã/õ/à/è/ì/ò/ù/ä/ë/ï/ö/ü/ç
    são preservados, então texto em português chega intacto ao classificador.
    """
    resultado = limpar_texto("Ótimo é a solução para acentuação")
    assert "ótimo" in resultado
    assert "é" in resultado
    assert "solução" in resultado
    assert "acentuação" in resultado
