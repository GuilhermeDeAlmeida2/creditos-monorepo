-- Script para criação do banco de dados
-- Execute este script como superusuário (postgres)

-- Criar o banco de dados se não existir
CREATE DATABASE creditos_db
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Comentário do banco
COMMENT ON DATABASE creditos_db IS 'Banco de dados para o sistema de créditos';

-- Conectar ao banco criado
\c creditos_db;

