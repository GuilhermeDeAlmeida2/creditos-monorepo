#!/bin/bash

# Script para executar o sistema completo de créditos do zero
# Requisitos: Docker, Docker Compose e PostgreSQL instalados localmente

set -e  # Parar em caso de erro

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Função para verificar se um comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Função para verificar se PostgreSQL está rodando
check_postgres() {
    print_status "Verificando se PostgreSQL está rodando..."
    
    if ! command_exists psql; then
        print_error "psql não encontrado. Certifique-se de que PostgreSQL está instalado."
        exit 1
    fi
    
    # Tentar conectar ao PostgreSQL
    if psql -h localhost -U postgres -d postgres -c '\q' 2>/dev/null; then
        print_success "PostgreSQL está rodando e acessível"
    else
        print_error "Não foi possível conectar ao PostgreSQL. Verifique se:"
        print_error "1. PostgreSQL está instalado e rodando"
        print_error "2. O usuário 'postgres' existe"
        print_error "3. A senha está configurada corretamente"
        print_error "4. O PostgreSQL está aceitando conexões locais"
        exit 1
    fi
}

# Função para executar scripts SQL
execute_sql_script() {
    local script_file="$1"
    local script_name=$(basename "$script_file")
    
    print_status "Executando script: $script_name"
    
    if [ ! -f "$script_file" ]; then
        print_error "Arquivo não encontrado: $script_file"
        exit 1
    fi
    
    # Executar o script SQL
    if psql -h localhost -U postgres -d postgres -f "$script_file"; then
        print_success "Script $script_name executado com sucesso"
    else
        print_error "Erro ao executar script $script_name"
        exit 1
    fi
}

# Função para verificar se Docker está rodando
check_docker() {
    print_status "Verificando se Docker está rodando..."
    
    if ! command_exists docker; then
        print_error "Docker não encontrado. Certifique-se de que Docker está instalado."
        exit 1
    fi
    
    if ! docker info >/dev/null 2>&1; then
        print_error "Docker não está rodando. Inicie o Docker e tente novamente."
        exit 1
    fi
    
    print_success "Docker está rodando"
}

# Função para verificar se Docker Compose está disponível
check_docker_compose() {
    print_status "Verificando Docker Compose..."
    
    if command_exists docker-compose; then
        COMPOSE_CMD="docker-compose"
    elif docker compose version >/dev/null 2>&1; then
        COMPOSE_CMD="docker compose"
    else
        print_error "Docker Compose não encontrado. Instale Docker Compose e tente novamente."
        exit 1
    fi
    
    print_success "Docker Compose disponível: $COMPOSE_CMD"
}

# Função para limpar containers e volumes existentes
cleanup_docker() {
    print_status "Limpando containers e volumes existentes..."
    
    # Parar containers se estiverem rodando
    cd infra
    $COMPOSE_CMD down --volumes --remove-orphans 2>/dev/null || true
    cd ..
    
    print_success "Limpeza concluída"
}

# Função para construir e subir os serviços
start_services() {
    print_status "Construindo e iniciando serviços..."
    
    cd infra
    
    # Construir as imagens
    print_status "Construindo imagens Docker..."
    $COMPOSE_CMD build --no-cache
    
    # Subir os serviços
    print_status "Iniciando serviços..."
    $COMPOSE_CMD up -d
    
    cd ..
    
    print_success "Serviços iniciados"
}

# Função para aguardar serviços ficarem prontos
wait_for_services() {
    print_status "Aguardando serviços ficarem prontos..."
    
    
    # Aguardar API
    print_status "Aguardando API..."
    timeout=120
    counter=0
    while ! curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            print_error "Timeout aguardando API ficar pronta"
            exit 1
        fi
        sleep 3
        counter=$((counter + 3))
    done
    print_success "API está pronta"
    
    # Aguardar Web
    print_status "Aguardando Web..."
    timeout=60
    counter=0
    while ! curl -f http://localhost:3000 >/dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            print_error "Timeout aguardando Web ficar pronta"
            exit 1
        fi
        sleep 2
        counter=$((counter + 2))
    done
    print_success "Web está pronta"
}


# Função para verificar status final
check_final_status() {
    print_status "Verificando status final dos serviços..."
    
    echo ""
    print_status "=== STATUS DOS SERVIÇOS ==="
    
    # Verificar containers
    if docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(api|web)"; then
        print_success "Todos os containers estão rodando"
    else
        print_error "Alguns containers não estão rodando"
        exit 1
    fi
    
    echo ""
    print_status "=== TESTANDO ENDPOINTS ==="
    
    # Testar API
    if curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; then
        print_success "API: http://localhost:8080 ✓"
    else
        print_error "API não está respondendo"
    fi
    
    # Testar Web
    if curl -f http://localhost:3000 >/dev/null 2>&1; then
        print_success "Web: http://localhost:3000 ✓"
    else
        print_error "Web não está respondendo"
    fi
    
    
    # Verificar banco de dados
    if psql -h localhost -U postgres -d creditos_db -c "SELECT COUNT(*) FROM credito;" >/dev/null 2>&1; then
        count=$(psql -h localhost -U postgres -d creditos_db -t -c "SELECT COUNT(*) FROM credito;" 2>/dev/null | tr -d ' ')
        print_success "Banco de dados: $count registros na tabela credito ✓"
    else
        print_error "Banco de dados não está acessível"
    fi
}

# Função para mostrar informações de acesso
show_access_info() {
    echo ""
    echo "=========================================="
    print_success "SISTEMA INICIADO COM SUCESSO!"
    echo "=========================================="
    echo ""
    print_status "Acesse os serviços:"
    echo "  🌐 Frontend: http://localhost:3000"
    echo "  🔧 API: http://localhost:8080"
    echo "  📚 Swagger: http://localhost:8080/swagger-ui.html"
    echo "  💊 Health Check: http://localhost:8080/actuator/health"
    echo ""
    print_status "Banco de Dados:"
    echo "  🗄️  Host: localhost:5432"
    echo "  📊 Database: creditos_db"
    echo "  👤 Usuário: postgres"
    echo ""
    print_status "Para parar o sistema:"
    echo "  cd infra && docker-compose down"
    echo ""
    print_status "Para ver logs:"
    echo "  cd infra && docker-compose logs -f [servico]"
    echo ""
}

# Função principal
main() {
    echo "=========================================="
    echo "🚀 INICIANDO SISTEMA DE CRÉDITOS"
    echo "=========================================="
    echo ""
    
    # Verificações iniciais
    check_postgres
    check_docker
    check_docker_compose
    
    echo ""
    print_status "=== CONFIGURANDO BANCO DE DADOS ==="
    
    # Executar scripts SQL na ordem correta
    execute_sql_script "database/00_create_user.sql"
    execute_sql_script "database/01_create_database.sql"
    execute_sql_script "database/02_create_table.sql"
    execute_sql_script "database/03_insert_data.sql"
    execute_sql_script "database/04_cleanup_duplicates.sql"
    execute_sql_script "database/05_add_constraints.sql"
    
    print_success "Banco de dados configurado com sucesso!"
    
    echo ""
    print_status "=== INICIANDO SERVIÇOS DOCKER ==="
    
    # Limpar e iniciar serviços
    cleanup_docker
    start_services
    
    # Aguardar serviços ficarem prontos
    wait_for_services
    
    
    # Verificar status final
    check_final_status
    
    # Mostrar informações de acesso
    show_access_info
}

# Verificar se o script está sendo executado diretamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
