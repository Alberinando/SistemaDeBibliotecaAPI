ALTER TABLE historicoemprestimos DROP CONSTRAINT historicoemprestimos_emprestimoid_fkey;

ALTER TABLE historicoemprestimos
    ADD CONSTRAINT historicoemprestimos_emprestimoid_fkey
        FOREIGN KEY (emprestimoId) REFERENCES emprestimos(id)
            ON DELETE CASCADE
            ON UPDATE CASCADE;