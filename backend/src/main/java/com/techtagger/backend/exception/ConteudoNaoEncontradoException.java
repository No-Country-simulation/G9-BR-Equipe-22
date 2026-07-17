package com.techtagger.backend.exception;

public class ConteudoNaoEncontradoException extends RuntimeException {
    public ConteudoNaoEncontradoException(Long id) {
        super("Conteúdo não encontrado: " + id);
    }
}