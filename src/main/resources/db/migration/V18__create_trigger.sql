-- Para Livros:
DROP TRIGGER IF EXISTS trg_atualizaupdatedat_livros ON livros;
CREATE TRIGGER trg_atualizaupdatedat_livros
    BEFORE UPDATE ON livros
    FOR EACH ROW
    EXECUTE FUNCTION AtualizaUpdatedAt();

-- Para Membros:
DROP TRIGGER IF EXISTS trg_atualizaupdatedat_membros ON membros;
CREATE TRIGGER trg_atualizaupdatedat_membros
    BEFORE UPDATE ON membros
    FOR EACH ROW
    EXECUTE FUNCTION AtualizaUpdatedAt();

-- Para Empréstimos:
DROP TRIGGER IF EXISTS trg_atualizaupdatedat_emprestimos ON emprestimos;
CREATE TRIGGER trg_atualizaupdatedat_emprestimos
    BEFORE UPDATE ON emprestimos
    FOR EACH ROW
    EXECUTE FUNCTION AtualizaUpdatedAt();

-- Para Funcionários:
DROP TRIGGER IF EXISTS trg_atualizaupdatedat_funcionarios ON funcionarios;
CREATE TRIGGER trg_atualizaupdatedat_funcionarios
    BEFORE UPDATE ON funcionarios
    FOR EACH ROW
    EXECUTE FUNCTION AtualizaUpdatedAt();

-- Para Histórico de Empréstimos:
DROP TRIGGER IF EXISTS trg_atualizaupdatedat_historico ON historico;
CREATE TRIGGER trg_atualizaupdatedat_historico
    BEFORE UPDATE ON historicoEmprestimos
    FOR EACH ROW
    EXECUTE FUNCTION AtualizaUpdatedAt();