CREATE TRIGGER trg_sync_historico_emprestimos
    AFTER INSERT OR UPDATE OR DELETE ON emprestimos
                        FOR EACH ROW
                        EXECUTE FUNCTION fn_sync_historico_emprestimos();