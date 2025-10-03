#!/bin/bash

# Script para consultar todos os registros da tabela credito
# Banco: creditos_db
# Tabela: credito

# Configurações do banco de dados
DB_HOST="localhost"
DB_PORT="5432"
DB_NAME="creditos_db"
DB_USER="postgres"
DB_PASSWORD="postgres123"

# Comando para consultar todos os registros
echo "🔍 Consultando todos os registros da tabela credito..."
echo "📊 Banco: $DB_NAME"
echo "🏠 Host: $DB_HOST:$DB_PORT"
echo "👤 Usuário: $DB_USER"
echo "----------------------------------------"

# Executar consulta SQL
PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "
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
    created_at,
    updated_at
FROM credito 
ORDER BY id;
"

# Verificar se a consulta foi executada com sucesso
if [ $? -eq 0 ]; then
    echo "----------------------------------------"
    echo "✅ Consulta executada com sucesso!"
else
    echo "❌ Erro ao executar a consulta."
    echo "💡 Verifique se:"
    echo "   - O PostgreSQL está rodando"
    echo "   - As credenciais estão corretas"
    echo "   - O banco creditos_db existe"
    echo "   - A tabela credito existe"
fi
