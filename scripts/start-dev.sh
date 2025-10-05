#!/bin/bash

# Script para iniciar o sistema em ambiente de desenvolvimento
echo "🚀 Iniciando sistema em ambiente de DESENVOLVIMENTO..."

cd infra

# Parar containers existentes
echo "🛑 Parando containers existentes..."
docker-compose -f docker-compose.dev.yml down

# Remover imagens antigas (opcional)
echo "🧹 Removendo imagens antigas..."
docker-compose -f docker-compose.dev.yml down --rmi all

# Construir e iniciar os serviços
echo "🔨 Construindo e iniciando serviços..."
docker-compose -f docker-compose.dev.yml up --build -d

echo "✅ Sistema iniciado em ambiente de desenvolvimento!"
echo "📊 API: http://localhost:8080"
echo "🌐 Frontend: http://localhost:3000"
echo "📚 Swagger: http://localhost:8080/swagger-ui.html"
echo ""
echo "🔧 Funcionalidades de teste HABILITADAS"
echo "   - Testar conexão"
echo "   - Gerar registros de teste"
echo "   - Deletar registros de teste"
