CREATE OR REPLACE FUNCTION atualiza_disponibilidade()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.quantidade = 0 THEN
        NEW.disponibilidade := FALSE;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

