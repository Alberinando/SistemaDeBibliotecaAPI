-- Adiciona campo quantidade emprestada na tabela emprestimos
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'emprestimos' AND column_name = 'quantidade') THEN
        ALTER TABLE emprestimos ADD COLUMN quantidade INTEGER NOT NULL DEFAULT 1;
    END IF;
END $$;
