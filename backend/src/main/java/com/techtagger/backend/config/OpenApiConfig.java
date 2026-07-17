package com.techtagger.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI techTaggerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🧠 TechTagger API — Backend")
                        .description("""
                                API pública (Java/Spring Boot) para organização inteligente de conteúdo técnico.

                                Recebe um conteúdo (título + texto), consome internamente a **ML API** \
                                (Python/FastAPI) para classificação e análise, e persiste o resultado no MySQL.

                                ## Funcionalidades
                                - 🏷️ **Classificação automática** de conteúdo por categoria técnica
                                - 🔑 **Extração de palavras-chave** relevantes
                                - 🔗 **Recomendação** de conteúdos relacionados por similaridade semântica
                                - 💾 **Persistência** dos conteúdos processados em banco de dados
                                - ✅ **Validação de entrada** e tratamento de erros

                                ## Arquitetura
                                Cliente → Backend (Java, aqui) → ML API (Python) → MySQL
                                     ## Tecnologias
                                - Java 25 + Spring Boot 4
                                - Spring Data JPA + Flyway (migrations)
                                - WebClient (integração com a ML API)
                                - MySQL 8
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe TechTagger")
                                .url("https://github.com/No-Country-simulation/G9-BR-Equipe-22"))
                        .license(new License()
                                .name("MIT")));
    }
}