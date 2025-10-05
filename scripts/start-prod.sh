#!/bin/bash

# Script para iniciar o sistema em ambiente de produção
echo "🚀 Iniciando sistema em ambiente de PRODUÇÃO..."

cd infra

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose -f docker-compose.prod.yml down

# Remover imagens antigas (opcional)
echo "🧹 Removendo imagens antigas..."
docker-compose -f docker-compose.prod.yml down --rmi all

# Construir e iniciar os serviços
echo "🔨 Construindo e iniciando serviços..."
docker-compose -f docker-compose.prod.yml up --build -d

echo "✅ Sistema iniciado em ambiente de produção!"
echo "📊 API: http://localhost:8080"
echo "🌐 Frontend: http://localhost:3000"
echo ""
echo "🔒 Funcionalidades de teste DESABILITADAS"
echo "   - Apenas funcionalidades de produção disponíveis"
echo "   - Swagger desabilitado"
echo "   - Logs reduzidos"
