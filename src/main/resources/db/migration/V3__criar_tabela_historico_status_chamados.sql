CREATE TABLE historico_status_chamados (
    id BIGSERIAL PRIMARY KEY,
    chamado_id BIGINT NOT NULL,
    status_anterior VARCHAR(30),
    status_novo VARCHAR(30) NOT NULL,
    data_alteracao TIMESTAMP NOT NULL,

    CONSTRAINT fk_historico_status_chamado
        FOREIGN KEY (chamado_id)
        REFERENCES chamados (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_historico_status_chamado_id
    ON historico_status_chamados (chamado_id);