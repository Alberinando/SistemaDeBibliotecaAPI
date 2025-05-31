CREATE TRIGGER trg_atualiza_disponibilidade
    BEFORE INSERT OR UPDATE OF quantidade ON livros
    FOR EACH ROW
    EXECUTE FUNCTION atualiza_disponibilidade();
