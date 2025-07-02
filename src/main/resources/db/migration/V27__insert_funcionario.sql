INSERT INTO funcionarios (nome, cargo, login, senha)
SELECT 'Admin', 'Adm', 'Adm', '$2a$10$0uA267QTFuH3N/12bTGPHehleaOXCpZGGF8Fni2mIoAl4WfYWlOpq'
    WHERE NOT EXISTS (
    SELECT 1 FROM public.funcionarios WHERE login = 'Adm'
);