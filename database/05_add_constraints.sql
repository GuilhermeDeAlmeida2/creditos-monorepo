-- Script para adicionar constraints de unicidade
-- Execute este script conectado ao banco creditos_db

-- Adicionar constraint de unicidade para numero_credito
-- (assumindo que cada número de crédito deve ser único)
ALTER TABLE credito 
ADD CONSTRAINT uk_credito_numero_credito 
UNIQUE (numero_credito);

-- Adicionar constraint de unicidade composta para evitar duplicatas
-- baseada em numero_credito + numero_nfse + data_constituicao + tipo_credito
ALTER TABLE credito 
ADD CONSTRAINT uk_credito_composto 
UNIQUE (numero_credito, numero_nfse, data_constituicao, tipo_credito);

-- Verificar constraints criadas
SELECT 
    conname as constraint_name,
    contype as constraint_type,
    pg_get_constraintdef(oid) as definition
FROM pg_constraint 
WHERE conrelid = 'credito'::regclass
AND contype = 'u'
ORDER BY conname;

-- Mostrar informações da tabela
\d credito;
