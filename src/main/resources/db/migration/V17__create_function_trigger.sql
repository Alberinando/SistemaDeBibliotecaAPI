--DROP FUNCTION IF EXISTS AtualizaUpdatedAt();

CREATE FUNCTION AtualizaUpdatedAt()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
