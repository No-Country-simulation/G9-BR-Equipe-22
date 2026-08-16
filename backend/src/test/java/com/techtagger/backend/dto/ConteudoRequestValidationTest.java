package com.techtagger.backend.dto;

import com.techtagger.backend.dto.request.ConteudoRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes puros de validação Bean Validation em ConteudoRequest — não
 * sobem contexto Spring, então rodam em milissegundos e servem de rede de
 * segurança para as regras de negócio de entrada da API pública.
 */
class ConteudoRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void aceitaTituloETextoValidos() {
        ConteudoRequest request = new ConteudoRequest(
                "Introdução ao Spring Boot",
                "Neste conteúdo são apresentados os conceitos básicos para criação de APIs REST.");

        Set<ConstraintViolation<ConteudoRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isEmpty();
    }

    @Test
    void rejeitaTituloVazio() {
        ConteudoRequest request = new ConteudoRequest("", "Um texto com mais de dez caracteres.");

        Set<ConstraintViolation<ConteudoRequest>> violacoes = validator.validate(request);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes)
                .anyMatch(v -> v.getPropertyPath().toString().equals("titulo"));
    }

    @Test
    void rejeitaTituloComMenosDeTresCaracteres() {
        ConteudoRequest request = new ConteudoRequest("ab", "Um texto com mais de dez caracteres.");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejeitaTextoComMenosDeDezCaracteres() {
        ConteudoRequest request = new ConteudoRequest("Titulo valido", "curto");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejeitaTituloNulo() {
        ConteudoRequest request = new ConteudoRequest(null, "Um texto com mais de dez caracteres.");

        assertThat(validator.validate(request)).isNotEmpty();
    }

    @Test
    void rejeitaTextoAcimaDoLimiteDeDezMilCaracteres() {
        String textoGigante = "a".repeat(10_001);
        ConteudoRequest request = new ConteudoRequest("Titulo valido", textoGigante);

        assertThat(validator.validate(request)).isNotEmpty();
    }
}
