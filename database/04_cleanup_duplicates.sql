-- Script para limpeza de registros duplicados
-- Execute este script conectado ao banco creditos_db

-- Verificar registros duplicados antes da limpeza
SELECT 
    numero_credito,
    numero_nfse,
    data_constituicao,
    tipo_credito,
    COUNT(*) as quantidade
FROM credito 
GROUP BY numero_credito, numero_nfse, data_constituicao, tipo_credito
HAVING COUNT(*) > 1
ORDER BY quantidade DESC;

-- Mostrar todos os registros duplicados
WITH duplicados AS (
    SELECT 
        id,
        numero_credito,
        numero_nfse,
        data_constituicao,
        tipo_credito,
        ROW_NUMBER() OVER (
            PARTITION BY numero_credito, numero_nfse, data_constituicao, tipo_credito 
            ORDER BY id
        ) as rn
    FROM credito
)
SELECT 
    id,
    numero_credito,
    numero_nfse,
    data_constituicao,
    tipo_credito,
    created_at,
    CASE 
        WHEN rn = 1 THEN 'MANTER'
        ELSE 'REMOVER'
    END as acao
FROM duplicados
WHERE (numero_credito, numero_nfse, data_constituicao, tipo_credito) IN (
    SELECT numero_credito, numero_nfse, data_constituicao, tipo_credito
    FROM credito 
    GROUP BY numero_credito, numero_nfse, data_constituicao, tipo_credito
    HAVING COUNT(*) > 1
)
ORDER BY numero_credito, numero_nfse, data_constituicao, tipo_credito, id;

-- Remover registros duplicados (mantendo apenas o primeiro de cada grupo)
WITH duplicados AS (
    SELECT 
        id,
        ROW_NUMBER() OVER (
            PARTITION BY numero_credito, numero_nfse, data_constituicao, tipo_credito 
            ORDER BY id
        ) as rn
    FROM credito
)
DELETE FROM credito 
WHERE id IN (
    SELECT id 
    FROM duplicados 
    WHERE rn > 1
);

-- Verificar se a limpeza foi bem-sucedida
SELECT 
    'Registros restantes após limpeza:' as status,
    COUNT(*) as total_registros
FROM credito;

-- Mostrar registros finais
SELECT 
    id,
    numero_credito,
    numero_nfse,
    data_constituicao,
    valor_issqn,
    tipo_credito,
    simples_nacional,
    created_at
FROM credito 
ORDER BY id;
