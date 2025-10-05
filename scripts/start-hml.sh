#!/bin/bash

# Script para iniciar o sistema em ambiente de homologação
echo "🚀 Iniciando sistema em ambiente de HOMOLOGAÇÃO..."

cd infra

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose -f docker-compose.hml.yml down

# Remover imagens antigas (opcional)
echo "🧹 Removendo imagens antigas..."
docker-compose -f docker-compose.hml.yml down --rmi all

# Construir e iniciar os serviços
echo "🔨 Construindo e iniciando serviços..."
docker-compose -f docker-compose.hml.yml up --build -d

echo "✅ Sistema iniciado em ambiente de homologação!"
echo "📊 API: http://localhost:8080"
echo "🌐 Frontend: http://localhost:3000"
echo "📚 Swagger: http://localhost:8080/swagger-ui.html"
echo ""
echo "🔒 Funcionalidades de teste DESABILITADAS"
echo "   - Apenas funcionalidades de produção disponíveis"
