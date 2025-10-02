-- Script para criação do usuário postgres com permissões adequadas
-- Execute este script como superusuário (usuário que criou o PostgreSQL)

-- Criar o usuário postgres se não existir
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres') THEN
        CREATE USER postgres WITH PASSWORD 'postgres123';
        RAISE NOTICE 'Usuário postgres criado com sucesso!';
    ELSE
        RAISE NOTICE 'Usuário postgres já existe.';
    END IF;
END $$;

-- Conceder permissões de superusuário ao postgres
ALTER USER postgres WITH SUPERUSER;

-- Conceder permissões para criar bancos de dados
ALTER USER postgres WITH CREATEDB;

-- Conceder permissões para criar usuários
ALTER USER postgres WITH CREATEROLE;

-- Conceder permissões para replicação (se necessário)
ALTER USER postgres WITH REPLICATION;

-- Verificar as permissões do usuário
SELECT 
    rolname as usuario,
    rolsuper as superusuario,
    rolcreatedb as pode_criar_banco,
    rolcreaterole as pode_criar_usuario,
    rolreplication as pode_replicar
FROM pg_roles 
WHERE rolname = 'postgres';

-- Mostrar informações do usuário
\du postgres;
