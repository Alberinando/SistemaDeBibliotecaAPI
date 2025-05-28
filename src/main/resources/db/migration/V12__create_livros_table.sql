CREATE TABLE IF NOT EXISTS livros (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo          VARCHAR(255)        NOT NULL,
    autor           VARCHAR(255)        NOT NULL,
    categoria       VARCHAR(100)        NOT NULL,
    disponibilidade BOOLEAN             NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);