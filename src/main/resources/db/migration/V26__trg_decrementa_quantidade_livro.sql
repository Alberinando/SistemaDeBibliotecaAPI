CREATE TRIGGER trg_decrementa_quantidade_livro
    AFTER INSERT ON emprestimos
    FOR EACH ROW
    EXECUTE FUNCTION decrementa_quantidade_livro();