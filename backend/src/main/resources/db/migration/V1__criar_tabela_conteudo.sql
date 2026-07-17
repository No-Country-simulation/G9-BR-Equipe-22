CREATE TABLE conteudo (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          titulo VARCHAR(500) NOT NULL,
                          texto TEXT NOT NULL,
                          categoria VARCHAR(100),
                          probabilidade DOUBLE,
                          keywords TEXT,
                          criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);