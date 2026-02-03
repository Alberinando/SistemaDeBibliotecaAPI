-- Adiciona campo de preferência de notificação automática para funcionários
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'funcionarios' AND column_name = 'notificacao_automatica') THEN
        ALTER TABLE funcionarios ADD COLUMN notificacao_automatica BOOLEAN NOT NULL DEFAULT true;
    END IF;
END $$;
