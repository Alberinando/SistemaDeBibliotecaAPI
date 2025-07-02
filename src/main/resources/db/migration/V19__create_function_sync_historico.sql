CREATE OR REPLACE FUNCTION fn_sync_historico_emprestimos()
    RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO historicoEmprestimos(emprestimoId, livroId, idMembro, dataAcao, created_at, updated_at)
        VALUES (NEW.id, NEW.livroId, NEW.membroId, NEW.dataEmprestimo, NEW.created_at, NEW.updated_at);
        RETURN NEW;

    ELSIF (TG_OP = 'UPDATE') THEN
        UPDATE historicoEmprestimos
        SET livroId    = NEW.livroId,
            idMembro   = NEW.membroId,
            dataAcao   = NEW.dataEmprestimo,
            updated_at = NEW.updated_at
        WHERE emprestimoId = NEW.id;
        RETURN NEW;

    ELSIF (TG_OP = 'DELETE') THEN
        DELETE FROM historicoEmprestimos
        WHERE emprestimoId = OLD.id;
        RETURN OLD;
    END IF;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;