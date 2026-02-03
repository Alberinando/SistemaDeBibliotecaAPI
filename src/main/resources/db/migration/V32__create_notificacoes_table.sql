-- Cria tabela de notificações pendentes
CREATE TABLE IF NOT EXISTS notificacoes (
    id SERIAL PRIMARY KEY,
    funcionario_id BIGINT NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    emprestimo_id BIGINT NOT NULL REFERENCES emprestimos(id) ON DELETE CASCADE,
    mensagem VARCHAR(500) NOT NULL,
    lida BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_notificacoes_funcionario ON notificacoes(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_notificacoes_lida ON notificacoes(funcionario_id, lida);
