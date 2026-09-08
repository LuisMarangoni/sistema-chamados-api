CREATE TABLE chamados (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    status VARCHAR(30) NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP NOT NULL
);