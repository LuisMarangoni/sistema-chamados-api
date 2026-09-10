ALTER TABLE chamados
    ADD COLUMN solicitante_id BIGINT;

CREATE INDEX idx_chamados_solicitante_id
    ON chamados (solicitante_id);
