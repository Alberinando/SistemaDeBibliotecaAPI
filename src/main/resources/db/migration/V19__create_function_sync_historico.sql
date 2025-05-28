CREATE OR REPLACE FUNCTION fn_sync_historico_emprestimos()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO historicoEmprestimos(livroId, idMembro, dataAcao, created_at, updated_at)
        VALUES (NEW.livroId, NEW.membroId, NEW.dataEmprestimo, NEW.created_at, NEW.updated_at);
RETURN NEW;
ELSIF (TG_OP = 'UPDATE') THEN
UPDATE historicoEmprestimos
SET livroId   = NEW.livroId,
    idMembro  = NEW.membroId,
    dataAcao  = NEW.dataEmprestimo,
    updated_at = NEW.updated_at
WHERE livroId = OLD.livroId AND idMembro = OLD.membroId;
RETURN NEW;
END IF;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;
