package com.techtagger.backend.controller;

import com.techtagger.backend.client.MLServiceClient;
import com.techtagger.backend.dto.response.ConteudoResponse;
import com.techtagger.backend.exception.ConteudoNaoEncontradoException;
import com.techtagger.backend.service.ConteudoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de fatia web (@WebMvcTest) para ConteudoController: sobem só a
 * camada MVC, com ConteudoService e MLServiceClient mockados via
 * @MockitoBean (substituto de @MockBean a partir do Spring
 * Framework 6.2 / Boot 3.4+, usado aqui porque este projeto está no
 * Spring Boot 4). Cobrem serialização JSON, roteamento e o status HTTP
 * devolvido para casos de sucesso, validação e "não encontrado".
 *
 * Nota: no Spring Boot 4, @WebMvcTest foi movida de
 * org.springframework.boot.test.autoconfigure.web.servlet (pacote do Boot 3.x)
 * para org.springframework.boot.webmvc.test.autoconfigure, acompanhando a
 * modularização do starter web (spring-boot-starter-webmvc). O import antigo
 * compilava no Boot 3.x mas não existe mais no Boot 4.1.0 — corrigido aqui.
 *
 * Também removido um campo @Autowired ObjectMapper que nunca era usado (os
 * corpos JSON das requisições são strings literais em .content(...), não
 * gerados via objectMapper.writeValueAsString(...)). No slice reduzido do
 * @WebMvcTest desse projeto, o bean ObjectMapper não fica disponível no
 * contexto, e como o campo era obrigatório, a injeção falhava e derrubava
 * os 5 testes desta classe mesmo sem nenhum deles precisar do ObjectMapper.
 */
@WebMvcTest(ConteudoController.class)
class ConteudoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConteudoService service;

    @MockitoBean
    private MLServiceClient mlServiceClient;

    @Test
    void postConteudoComDadosValidosRetorna200() throws Exception {
        ConteudoResponse resposta = new ConteudoResponse(
                1L, "Titulo", "Texto com mais de dez caracteres.", "Backend", 0.9, List.of("k1"), List.of());
        when(service.processar(any())).thenReturn(resposta);

        mockMvc.perform(post("/conteudo")
                        .contentType("application/json")
                        .content("{\"titulo\": \"Titulo\", \"texto\": \"Texto com mais de dez caracteres.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("Backend"));
    }

    @Test
    void postConteudoComTituloVazioRetorna400() throws Exception {
        mockMvc.perform(post("/conteudo")
                        .contentType("application/json")
                        .content("{\"titulo\": \"\", \"texto\": \"Texto com mais de dez caracteres.\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void getConteudoPorIdInexistenteRetorna404() throws Exception {
        when(service.buscarPorId(anyLong())).thenThrow(new ConteudoNaoEncontradoException(999L));

        mockMvc.perform(get("/conteudo/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getConteudoListaComPaginacao() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(service.listar(any(), any())).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        mockMvc.perform(get("/conteudo"))
                .andExpect(status().isOk());
    }

    @Test
    void getConteudoCategoriasDelegaParaMLServiceClient() throws Exception {
        when(mlServiceClient.listarCategorias()).thenReturn(List.of("Backend", "Frontend"));

        mockMvc.perform(get("/conteudo/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Backend"));
    }
}
