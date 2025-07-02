CREATE TABLE IF NOT EXISTS historicoEmprestimos (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    emprestimoId    BIGINT      NOT NULL UNIQUE REFERENCES emprestimos(id),
    livroId         BIGINT      NOT NULL REFERENCES livros(id),
    idMembro        BIGINT      NOT NULL REFERENCES membros(id),
    dataAcao        TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
