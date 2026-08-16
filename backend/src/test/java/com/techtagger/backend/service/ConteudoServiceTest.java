package com.techtagger.backend.service;

import com.techtagger.backend.client.MLServiceClient;
import com.techtagger.backend.dto.ml.CategoryScore;
import com.techtagger.backend.dto.ml.MLBatchResponse;
import com.techtagger.backend.dto.ml.MLBatchResponseItem;
import com.techtagger.backend.dto.ml.MLResponse;
import com.techtagger.backend.dto.ml.RelatedContent;
import com.techtagger.backend.dto.request.ConteudoBatchRequest;
import com.techtagger.backend.dto.request.ConteudoRequest;
import com.techtagger.backend.dto.response.ConteudoResponse;
import com.techtagger.backend.exception.ConteudoNaoEncontradoException;
import com.techtagger.backend.model.Conteudo;
import com.techtagger.backend.repository.ConteudoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários de ConteudoService com MLServiceClient e
 * ConteudoRepository mockados — cobrem a regra de negócio central do
 * backend sem precisar subir Spring, banco ou a ml-api de verdade.
 */
@ExtendWith(MockitoExtension.class)
class ConteudoServiceTest {

    @Mock
    private MLServiceClient mlServiceClient;

    @Mock
    private ConteudoRepository repository;

    private ConteudoService service;

    @BeforeEach
    void setUp() {
        service = new ConteudoService(mlServiceClient, repository);
    }

    private MLResponse mlResponsePadrao() {
        return new MLResponse(
                "Backend",
                0.91,
                List.of(new CategoryScore("Backend", 0.91)),
                List.of("Spring Boot", "Java"),
                List.of(new RelatedContent("Outro artigo", "Backend", 0.87, null)),
                LocalDateTime.now());
    }

    @Test
    void processarClassificaESalvaOConteudo() {
        ConteudoRequest request = new ConteudoRequest("Introdução ao Spring Boot", "Texto com mais de dez caracteres.");
        when(mlServiceClient.classificar(request.titulo(), request.texto())).thenReturn(mlResponsePadrao());
        when(repository.save(any(Conteudo.class))).thenAnswer(invocation -> {
            Conteudo c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        ConteudoResponse resposta = service.processar(request);

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.categoria()).isEqualTo("Backend");
        assertThat(resposta.probabilidade()).isEqualTo(0.91);
        assertThat(resposta.keywords()).containsExactly("Spring Boot", "Java");

        ArgumentCaptor<Conteudo> captor = ArgumentCaptor.forClass(Conteudo.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTitulo()).isEqualTo(request.titulo());
        assertThat(captor.getValue().getCategoria()).isEqualTo("Backend");
    }

    @Test
    void buscarPorIdRetornaConteudoQuandoExiste() {
        Conteudo conteudo = new Conteudo();
        conteudo.setId(5L);
        conteudo.setTitulo("Titulo");
        conteudo.setTexto("Texto");
        conteudo.setCategoria("Frontend");
        conteudo.setProbabilidade(0.8);
        conteudo.setKeywords(List.of("Vue"));
        when(repository.findById(5L)).thenReturn(Optional.of(conteudo));

        ConteudoResponse resposta = service.buscarPorId(5L);

        assertThat(resposta.id()).isEqualTo(5L);
        assertThat(resposta.categoria()).isEqualTo("Frontend");
    }

    @Test
    void buscarPorIdLancaExcecaoQuandoNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(999L))
                .isInstanceOf(ConteudoNaoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    void listarSemCategoriaChamaFindAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conteudo> pagina = new PageImpl<>(List.of(new Conteudo()));
        when(repository.findAll(pageable)).thenReturn(pagina);

        Page<Conteudo> resultado = service.listar(null, pageable);

        assertThat(resultado).isEqualTo(pagina);
        verify(repository).findAll(pageable);
    }

    @Test
    void listarComCategoriaChamaFindByCategoria() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Conteudo> pagina = new PageImpl<>(List.of(new Conteudo()));
        when(repository.findByCategoria(eq("Backend"), eq(pageable))).thenReturn(pagina);

        Page<Conteudo> resultado = service.listar("Backend", pageable);

        assertThat(resultado).isEqualTo(pagina);
        verify(repository).findByCategoria("Backend", pageable);
    }

    @Test
    void listarComCategoriaEmBrancoTrataComoSemFiltro() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        service.listar("   ", pageable);

        verify(repository).findAll(pageable);
    }

    @Test
    void processarLoteSalvaCadaItemEAgregaResultado() {
        ConteudoRequest item1 = new ConteudoRequest("Titulo 1", "Texto com mais de dez caracteres.");
        ConteudoRequest item2 = new ConteudoRequest("Titulo 2", "Texto com mais de dez caracteres.");
        ConteudoBatchRequest batchRequest = new ConteudoBatchRequest(List.of(item1, item2));

        MLBatchResponseItem resultadoItem1 = new MLBatchResponseItem(
                "Titulo 1", "Backend", 0.9, List.of(), List.of("k1"), List.of());
        MLBatchResponseItem resultadoItem2 = new MLBatchResponseItem(
                "Titulo 2", "Frontend", 0.8, List.of(), List.of("k2"), List.of());
        when(mlServiceClient.classificarLote(any()))
                .thenReturn(new MLBatchResponse(2, List.of(resultadoItem1, resultadoItem2)));
        when(repository.save(any(Conteudo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resposta = service.processarLote(batchRequest);

        assertThat(resposta.total()).isEqualTo(2);
        assertThat(resposta.results()).hasSize(2);
        assertThat(resposta.results().get(0).categoria()).isEqualTo("Backend");
        assertThat(resposta.results().get(1).categoria()).isEqualTo("Frontend");
        verify(repository, org.mockito.Mockito.times(2)).save(any(Conteudo.class));
    }
}
