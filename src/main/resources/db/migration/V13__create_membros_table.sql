CREATE TABLE IF NOT EXISTS membros (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome     VARCHAR(255)             NOT NULL,
    cpf      BIGINT                   NOT NULL UNIQUE,
    telefone BIGINT                   NOT NULL,
    email    VARCHAR(255)             NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);
