package com.techtagger.backend.service;

import com.techtagger.backend.client.MLServiceClient;
import com.techtagger.backend.dto.ml.MLResponse;
import com.techtagger.backend.dto.request.ConteudoRequest;
import com.techtagger.backend.dto.response.ConteudoResponse;
import com.techtagger.backend.model.Conteudo;
import com.techtagger.backend.repository.ConteudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConteudoService {

    @Autowired
    private MLServiceClient mlServiceClient;

    @Autowired
    private ConteudoRepository repository;

    public ConteudoResponse processar(ConteudoRequest request) {
        MLResponse resultado = mlServiceClient.classificar(request.titulo(), request.texto());

        Conteudo conteudo = new Conteudo();
        conteudo.setTitulo(request.titulo());
        conteudo.setTexto(request.texto());
        conteudo.setCategoria(resultado.category());
        conteudo.setProbabilidade(resultado.probability());
        conteudo.setKeywords(resultado.keywords());
        repository.save(conteudo);

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
                .orElseThrow(() -> new RuntimeException("Conteúdo não encontrado: " + id));

        return new ConteudoResponse(
                conteudo.getId(),
                conteudo.getCategoria(),
                conteudo.getProbabilidade(),
                conteudo.getKeywords(),
                null
        );
    }

    public List<Conteudo> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }
}