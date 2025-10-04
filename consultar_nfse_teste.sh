#!/bin/bash

# Script para consultar os dados de teste das NFS-e geradas
# Este script demonstra como consultar os registros de teste criados

API_BASE_URL="http://localhost:8080"

echo "=== CONSULTA DE DADOS DE TESTE - NFS-e ==="
echo "API Base URL: $API_BASE_URL"
echo ""

# Função para fazer requisições HTTP
make_request() {
    local method=$1
    local url=$2
    local description=$3
    
    echo "🔍 $description"
    echo "URL: $method $url"
    echo "---"
    
    if [ "$method" = "GET" ]; then
        curl -s -w "\nStatus: %{http_code}\n" "$url" | jq '.' 2>/dev/null || echo "Resposta (sem JSON):"
        curl -s "$url"
    else
        curl -s -X "$method" -w "\nStatus: %{http_code}\n" "$url" | jq '.' 2>/dev/null || echo "Resposta (sem JSON):"
        curl -s -X "$method" "$url"
    fi
    
    echo ""
    echo "========================================"
    echo ""
}

# 1. Consultar a primeira NFS-e (TESTE_NFSE001) - primeira página
make_request "GET" "$API_BASE_URL/api/creditos/paginated/TESTE_NFSE001?page=0&size=10" "Consulta NFS-e TESTE_NFSE001 - Primeira página (10 registros)"

# 2. Consultar a primeira NFS-e (TESTE_NFSE001) - segunda página
make_request "GET" "$API_BASE_URL/api/creditos/paginated/TESTE_NFSE001?page=1&size=10" "Consulta NFS-e TESTE_NFSE001 - Segunda página (10 registros)"

# 3. Consultar a primeira NFS-e (TESTE_NFSE001) - terceira página
make_request "GET" "$API_BASE_URL/api/creditos/paginated/TESTE_NFSE001?page=2&size=10" "Consulta NFS-e TESTE_NFSE001 - Terceira página (10 registros)"

# 4. Consultar uma NFS-e do meio (TESTE_NFSE005)
make_request "GET" "$API_BASE_URL/api/creditos/paginated/TESTE_NFSE005?page=0&size=15" "Consulta NFS-e TESTE_NFSE005 - Primeira página (15 registros)"

# 5. Consultar a última NFS-e (TESTE_NFSE010)
make_request "GET" "$API_BASE_URL/api/creditos/paginated/TESTE_NFSE010?page=0&size=30" "Consulta NFS-e TESTE_NFSE010 - Todos os 30 registros"

# 6. Consultar um crédito específico
make_request "GET" "$API_BASE_URL/api/creditos/credito/TESTE000001" "Consulta crédito específico TESTE000001"

# 7. Consultar um crédito do meio
make_request "GET" "$API_BASE_URL/api/creditos/credito/TESTE000150" "Consulta crédito específico TESTE000150"

# 8. Consultar um crédito da última NFS-e
make_request "GET" "$API_BASE_URL/api/creditos/credito/TESTE000300" "Consulta crédito específico TESTE000300"

echo "=== RESUMO DAS CONSULTAS ==="
echo "✅ TESTE_NFSE001: 30 créditos (TESTE000001 a TESTE000030)"
echo "✅ TESTE_NFSE002: 30 créditos (TESTE000031 a TESTE000060)"
echo "✅ TESTE_NFSE003: 30 créditos (TESTE000061 a TESTE000090)"
echo "✅ TESTE_NFSE004: 30 créditos (TESTE000091 a TESTE000120)"
echo "✅ TESTE_NFSE005: 30 créditos (TESTE000121 a TESTE000150)"
echo "✅ TESTE_NFSE006: 30 créditos (TESTE000151 a TESTE000180)"
echo "✅ TESTE_NFSE007: 30 créditos (TESTE000181 a TESTE000210)"
echo "✅ TESTE_NFSE008: 30 créditos (TESTE000211 a TESTE000240)"
echo "✅ TESTE_NFSE009: 30 créditos (TESTE000241 a TESTE000270)"
echo "✅ TESTE_NFSE010: 30 créditos (TESTE000271 a TESTE000300)"
echo ""
echo "Total: 10 NFS-e com 300 créditos"
