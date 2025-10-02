-- Script para inserção dos dados iniciais
-- Execute este script conectado ao banco creditos_db

-- Verificar se já existem dados na tabela
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM credito LIMIT 1) THEN
        RAISE NOTICE 'Dados já existem na tabela credito. Pulando inserção.';
    ELSE
        -- Inserir dados iniciais na tabela credito
        INSERT INTO credito (
            numero_credito, 
            numero_nfse, 
            data_constituicao, 
            valor_issqn,
            tipo_credito, 
            simples_nacional, 
            aliquota, 
            valor_faturado, 
            valor_deducao, 
            base_calculo
        ) VALUES
            ('123456', '7891011', '2024-02-25', 1500.75, 'ISSQN', true, 5.0, 30000.00, 5000.00, 25000.00),
            ('789012', '7891011', '2024-02-26', 1200.50, 'ISSQN', false, 4.5, 25000.00, 4000.00, 21000.00),
            ('654321', '1122334', '2024-01-15', 800.50, 'Outros', true, 3.5, 20000.00, 3000.00, 17000.00);
        
        RAISE NOTICE 'Dados iniciais inseridos com sucesso!';
    END IF;
END $$;

-- Verificar se os dados foram inseridos corretamente
SELECT 
    id,
    numero_credito,
    numero_nfse,
    data_constituicao,
    valor_issqn,
    tipo_credito,
    simples_nacional,
    aliquota,
    valor_faturado,
    valor_deducao,
    base_calculo,
    created_at
FROM credito 
ORDER BY id;

