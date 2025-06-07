CREATE OR REPLACE FUNCTION decrementa_quantidade_livro()
RETURNS TRIGGER AS $$
DECLARE
current_qtd INTEGER;
BEGIN
SELECT quantidade INTO current_qtd
FROM livros
WHERE id = NEW.livroid;

IF current_qtd > 0 THEN
UPDATE livros
SET quantidade = current_qtd - 1
WHERE id = NEW.livroid;
ELSE
UPDATE livros
SET disponibilidade = FALSE
WHERE id = NEW.livroid;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;