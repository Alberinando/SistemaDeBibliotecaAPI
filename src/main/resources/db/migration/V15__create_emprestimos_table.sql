CREATE TABLE IF NOT EXISTS emprestimos (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    livroId                 BIGINT      NOT NULL REFERENCES livros(id),
    membroId                BIGINT      NOT NULL REFERENCES membros(id),
    dataEmprestimo          TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    dataDevolucao           TIMESTAMP WITHOUT TIME ZONE,
    status                  BOOLEAN NOT NULL,
    created_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
