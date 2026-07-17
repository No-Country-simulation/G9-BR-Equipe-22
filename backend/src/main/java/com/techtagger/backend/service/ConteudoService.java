package com.techtagger.backend.service;

import com.techtagger.backend.client.MLServiceClient;
import com.techtagger.backend.dto.ml.MLResponse;
import com.techtagger.backend.dto.request.ConteudoRequest;
import com.techtagger.backend.dto.response.ConteudoResponse;
import com.techtagger.backend.exception.ConteudoNaoEncontradoException;
import com.techtagger.backend.model.Conteudo;
import com.techtagger.backend.repository.ConteudoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ConteudoService {

    private static final Logger log = LoggerFactory.getLogger(ConteudoService.class);

    private final MLServiceClient mlServiceClient;
    private final ConteudoRepository repository;

    public ConteudoService(MLServiceClient mlServiceClient, ConteudoRepository repository) {
        this.mlServiceClient = mlServiceClient;
        this.repository = repository;
    }

    public ConteudoResponse processar(ConteudoRequest request) {
        log.info("Processando conteúdo: titulo='{}'", request.titulo());

        MLResponse resultado = mlServiceClient.classificar(request.titulo(), request.texto());

        Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(request.titulo());
        conteudo.setTexto(request.texto());
        conteudo.setCategoria(resultado.category());
        conteudo.setProbabilidade(resultado.probability());
        conteudo.setKeywords(resultado.keywords());
        repository.save(conteudo);

        log.info("Conteúdo salvo com id={} categoria={}", conteudo.getId(), conteudo.getCategoria());

        return new ConteudoResponse(
                conteudo.getId(),
                resultado.category(),
                resultado.probability(),
                resultado.keywords(),
                resultado.relatedContent()
        );
    }

    public ConteudoResponse buscarPorId(Long id) {
        Conteudo conteudo = repository.findById(id)
                .orElseThrow(() -> new ConteudoNaoEncontradoException(id));

        return new ConteudoResponse(
                conteudo.getId(),
                conteudo.getCategoria(),
                conteudo.getProbabilidade(),
                conteudo.getKeywords(),
                null
        );
    }

    public Page<Conteudo> listar(String categoria, Pageable pageable) {
        if (categoria != null && !categoria.isBlank()) {
            return repository.findByCategoria(categoria, pageable);
        }
        return repository.findAll(pageable);
    }
}