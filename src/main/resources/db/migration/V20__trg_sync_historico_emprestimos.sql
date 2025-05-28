CREATE TRIGGER trg_sync_historico_emprestimos
    AFTER INSERT OR UPDATE ON emprestimos
                        FOR EACH ROW
                        EXECUTE FUNCTION fn_sync_historico_emprestimos();