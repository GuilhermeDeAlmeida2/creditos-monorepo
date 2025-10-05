#!/bin/bash

# Script para executar o sistema completo de créditos do zero
# Requisitos: Docker, Docker Compose, PostgreSQL LOCAL
# PostgreSQL: OBRIGATÓRIO - será iniciado automaticamente se necessário
#
# Uso:
#   ./executar_sistema_completo.sh                    # Modo interativo
#   ./executar_sistema_completo.sh --non-interactive  # Modo não-interativo
#
# Funcionalidades:
#   - Inicia PostgreSQL automaticamente (macOS/Linux)
#   - Solicita credenciais se necessário (modo interativo)
#   - Verifica existência de banco/tabelas
#   - Solicita permissão para criar/recriar banco/tabelas
#   - Usa credenciais padrão: postgres/postgres (porta 5432)

# Configuração de tratamento de erros
set -e  # Parar em caso de erro

# Função para tratamento de erros
handle_error() {
    local line_number=$1
    local error_code=$2
    print_error "Erro na linha $line_number (código: $error_code)"
    print_error "Para debug, execute: bash -x $0"
    
    print_warning "Para limpar containers Docker:"
    print_warning "  cd infra && docker-compose down"
    
    exit $error_code
}

# Configurar trap para capturar erros
trap 'handle_error $LINENO $?' ERR

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

# Variáveis globais para configuração do banco
USE_DOCKER_POSTGRES=false
POSTGRES_HOST="localhost"
POSTGRES_PORT="5432"
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="postgres"
POSTGRES_DB="postgres"

# Função para iniciar PostgreSQL automaticamente
start_postgres() {
    print_status "Tentando iniciar PostgreSQL automaticamente..."
    
    # Detectar sistema operacional e iniciar PostgreSQL
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        if command_exists brew; then
            print_status "Iniciando PostgreSQL via Homebrew..."
            brew services start postgresql
            sleep 3
        else
            print_warning "Homebrew não encontrado. Tente iniciar PostgreSQL manualmente."
            return 1
        fi
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        if command_exists systemctl; then
            print_status "Iniciando PostgreSQL via systemctl..."
            sudo systemctl start postgresql
            sleep 3
        elif command_exists service; then
            print_status "Iniciando PostgreSQL via service..."
            sudo service postgresql start
            sleep 3
        else
            print_warning "Sistema de serviço não encontrado. Tente iniciar PostgreSQL manualmente."
            return 1
        fi
    else
        print_warning "Sistema operacional não suportado para inicialização automática."
        return 1
    fi
    
    # Verificar se conseguiu iniciar
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h localhost -U postgres -d postgres -c '\q' 2>/dev/null; then
        print_success "PostgreSQL iniciado com sucesso!"
        return 0
    else
        print_warning "Falha ao iniciar PostgreSQL automaticamente."
        return 1
    fi
}

# Função para solicitar credenciais do PostgreSQL
request_postgres_credentials() {
    print_warning "PostgreSQL não está acessível com as credenciais padrão."
    echo ""
    
    if [[ "$INTERACTIVE_MODE" == "false" ]]; then
        print_error "Modo não-interativo ativo. Não é possível solicitar credenciais."
        print_error "Configure PostgreSQL local com usuário 'postgres' e senha 'postgres'."
        exit 1
    fi
    
    print_status "Por favor, informe as credenciais do PostgreSQL:"
    echo ""
    
    read -p "Host (padrão: localhost): " input_host
    POSTGRES_HOST=${input_host:-localhost}
    
    read -p "Porta (padrão: 5432): " input_port
    POSTGRES_PORT=${input_port:-5432}
    
    read -p "Usuário (padrão: postgres): " input_user
    POSTGRES_USER=${input_user:-postgres}
    
    read -s -p "Senha: " input_password
    echo ""
    POSTGRES_PASSWORD=${input_password:-postgres}
    
    print_status "Testando conexão com as credenciais fornecidas..."
    
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d postgres -c '\q' 2>/dev/null; then
        print_success "Conexão estabelecida com sucesso!"
        return 0
    else
        print_error "Falha ao conectar com as credenciais fornecidas."
        return 1
    fi
}

# Função para verificar se o banco creditos_db existe
check_database_exists() {
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d postgres -t -c "SELECT 1 FROM pg_database WHERE datname='creditos_db';" 2>/dev/null | grep -q 1; then
        return 0
    else
        return 1
    fi
}

# Função para verificar se a tabela credito existe
check_table_exists() {
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d creditos_db -t -c "SELECT 1 FROM information_schema.tables WHERE table_name='credito';" 2>/dev/null | grep -q 1; then
        return 0
    else
        return 1
    fi
}

# Função para solicitar permissão para criar banco/tabelas
request_database_creation_permission() {
    if [[ "$INTERACTIVE_MODE" == "false" ]]; then
        print_status "Modo não-interativo: assumindo permissão para criar banco/tabelas."
        return 0
    fi
    
    echo ""
    print_warning "O banco de dados 'creditos_db' ou suas tabelas não existem."
    print_status "Este script precisa criar/recriar o banco e as tabelas para funcionar corretamente."
    echo ""
    
    while true; do
        read -p "Deseja permitir a criação/recriação do banco de dados e tabelas? (s/n): " yn
        case $yn in
            [Ss]* ) 
                print_success "Permissão concedida. Criando banco e tabelas..."
                return 0
                ;;
            [Nn]* ) 
                print_error "Operação cancelada pelo usuário."
                exit 1
                ;;
            * ) 
                echo "Por favor, responda 's' para sim ou 'n' para não."
                ;;
        esac
    done
}

# Função para verificar se PostgreSQL está rodando
check_postgres() {
    print_status "Verificando PostgreSQL LOCAL (obrigatório)..."
    
    # Verificar se PostgreSQL client está instalado
    if ! command_exists psql; then
        print_error "PostgreSQL client não encontrado!"
        print_error "Instale PostgreSQL local:"
        print_error "  macOS: brew install postgresql"
        print_error "  Linux: sudo apt install postgresql-client (Ubuntu/Debian) ou sudo yum install postgresql (CentOS/RHEL)"
        print_error "  Windows: Baixe do site oficial do PostgreSQL"
        exit 1
    fi
    
    print_status "PostgreSQL client encontrado, testando conexão local..."
    
    # Tentar conectar ao PostgreSQL local com credenciais padrão
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h "$POSTGRES_HOST" -U "$POSTGRES_USER" -d postgres -c '\q' 2>/dev/null; then
        print_success "PostgreSQL LOCAL está rodando e acessível com credenciais padrão"
        USE_DOCKER_POSTGRES=false
        return 0
    else
        print_warning "PostgreSQL LOCAL não está acessível com credenciais padrão."
        
        # Tentar iniciar PostgreSQL automaticamente
        if start_postgres; then
            print_success "PostgreSQL iniciado e acessível!"
            return 0
        fi
        
        # Se não conseguiu iniciar automaticamente, solicitar credenciais
        print_warning "Tentando conectar com credenciais personalizadas..."
        if request_postgres_credentials; then
            return 0
        else
            print_error "Não foi possível estabelecer conexão com PostgreSQL."
            print_error "Configure PostgreSQL local:"
            print_error "1. Instale PostgreSQL:"
            print_error "   macOS: brew install postgresql"
            print_error "   Linux: sudo apt install postgresql (Ubuntu/Debian) ou sudo yum install postgresql (CentOS/RHEL)"
            print_error ""
            print_error "2. Inicie o serviço PostgreSQL:"
            print_error "   macOS: brew services start postgresql"
            print_error "   Linux: sudo systemctl start postgresql"
            print_error ""
            print_error "3. Configure usuário postgres (se necessário):"
            print_error "   sudo -u postgres psql -c \"ALTER USER postgres PASSWORD 'postgres';\""
            print_error ""
            print_error "4. Teste a conexão:"
            print_error "   psql -h localhost -U postgres -d postgres"
            print_error ""
            print_error "5. Execute novamente este script"
            exit 1
        fi
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
    
    # Para scripts que criam banco de dados, executar no banco postgres
    # Para outros scripts, executar no banco creditos_db
    local target_db="postgres"
    if [[ "$script_name" == "02_create_table.sql" ]] || [[ "$script_name" == "03_insert_data.sql" ]] || [[ "$script_name" == "04_cleanup_duplicates.sql" ]] || [[ "$script_name" == "05_add_constraints.sql" ]]; then
        target_db="creditos_db"
    fi
    
    # Executar o script SQL no PostgreSQL local
    local psql_cmd="psql -h $POSTGRES_HOST -p $POSTGRES_PORT -U $POSTGRES_USER -d $target_db -f $script_file"
    
    if [[ "$POSTGRES_PASSWORD" != "" ]]; then
        # Usar variável de ambiente para senha (mais seguro)
        export PGPASSWORD="$POSTGRES_PASSWORD"
    fi
    
    if $psql_cmd; then
        print_success "Script $script_name executado com sucesso"
    else
        print_error "Erro ao executar script $script_name"
        print_error "Comando executado: $psql_cmd"
        print_error "Host: $POSTGRES_HOST, Port: $POSTGRES_PORT, User: $POSTGRES_USER, Database: $target_db"
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
    
    # Remover containers específicos que podem estar conflitando
    print_status "Removendo containers conflitantes..."
    docker rm -f zookeeper kafka infra-api-1 infra-web-1 2>/dev/null || true
    
    # Limpar imagens órfãs
    print_status "Limpando imagens órfãs..."
    docker image prune -f 2>/dev/null || true
    
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
    
    # Aguardar Kafka
    print_status "Aguardando Kafka..."
    timeout=60
    counter=0
    while ! docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list >/dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            print_error "Timeout aguardando Kafka ficar pronto"
            exit 1
        fi
        sleep 2
        counter=$((counter + 2))
    done
    print_success "Kafka está pronto"
    
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

# Função para criar tópico Kafka para auditoria
create_kafka_topic() {
    print_status "Criando tópico Kafka para auditoria..."
    
    # Criar tópico se não existir
    docker exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic creditos-audit-events --partitions 3 --replication-factor 1 --if-not-exists
    
    print_success "Tópico 'creditos-audit-events' criado/verificado"
}

# Função para verificar status final
check_final_status() {
    print_status "Verificando status final dos serviços..."
    
    echo ""
    print_status "=== STATUS DOS SERVIÇOS ==="
    
    # Verificar containers
    if docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep -E "(kafka|zookeeper|api|web)"; then
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
    
    # Testar Kafka
    if docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list | grep -q "creditos-audit-events"; then
        print_success "Kafka: Tópico de auditoria criado ✓"
    else
        print_error "Kafka: Tópico de auditoria não encontrado"
    fi
    
    # Verificar banco de dados
    export PGPASSWORD="$POSTGRES_PASSWORD"
    if psql -h $POSTGRES_HOST -p $POSTGRES_PORT -U $POSTGRES_USER -d creditos_db -c "SELECT COUNT(*) FROM credito;" >/dev/null 2>&1; then
        count=$(psql -h $POSTGRES_HOST -p $POSTGRES_PORT -U $POSTGRES_USER -d creditos_db -t -c "SELECT COUNT(*) FROM credito;" 2>/dev/null | tr -d ' ')
        print_success "Banco de dados: $count registros na tabela credito ✓"
    else
        print_error "Banco de dados não está acessível"
        print_error "Host: $POSTGRES_HOST, Port: $POSTGRES_PORT, User: $POSTGRES_USER"
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
    print_status "Kafka (Auditoria):"
    echo "  🚀 Bootstrap Servers: localhost:9092"
    echo "  📝 Tópico de Auditoria: creditos-audit-events"
    echo ""
    print_status "Banco de Dados:"
    echo "  🗄️  Host: $POSTGRES_HOST:$POSTGRES_PORT"
    echo "  📊 Database: creditos_db"
    echo "  👤 Usuário: $POSTGRES_USER"
    echo "  💻 Modo: PostgreSQL LOCAL"
    echo ""
    print_status "Para parar o sistema:"
    echo "  cd infra && docker-compose down"
    echo ""
    print_status "Para ver logs:"
    echo "  cd infra && docker-compose logs -f [servico]"
    echo ""
}

# Função para processar argumentos
process_args() {
    INTERACTIVE_MODE="true"
    
    while [[ $# -gt 0 ]]; do
        case $1 in
            --non-interactive)
                INTERACTIVE_MODE="false"
                shift
                ;;
            --help|-h)
                echo "Uso: $0 [OPÇÕES]"
                echo ""
                echo "Opções:"
                echo "  --non-interactive    Modo não-interativo (assume credenciais padrão)"
                echo "  --help, -h          Mostra esta ajuda"
                echo ""
                echo "Funcionalidades:"
                echo "  - Inicia PostgreSQL automaticamente (macOS/Linux)"
                echo "  - Solicita credenciais se necessário (modo interativo)"
                echo "  - Verifica e cria banco/tabelas com permissão do usuário"
                echo "  - Credenciais padrão: postgres/postgres (porta 5432)"
                echo ""
                echo "Exemplos:"
                echo "  $0                  # Modo interativo (recomendado)"
                echo "  $0 --non-interactive # Modo não-interativo"
                exit 0
                ;;
            *)
                print_error "Opção desconhecida: $1"
                print_error "Use --help para ver as opções disponíveis"
                exit 1
                ;;
        esac
    done
}

# Função principal
main() {
    # Processar argumentos
    process_args "$@"
    
    echo "=========================================="
    echo "🚀 INICIANDO SISTEMA DE CRÉDITOS"
    echo "=========================================="
    echo ""
    
    if [[ "$INTERACTIVE_MODE" == "true" ]]; then
        print_status "Modo: Interativo"
    else
        print_status "Modo: Não-interativo"
    fi
    echo ""
    
    # Verificações iniciais
    check_postgres
    check_docker
    check_docker_compose
    
    echo ""
    print_status "=== VERIFICANDO BANCO DE DADOS LOCAL ==="
    
    # Verificar se o banco creditos_db existe
    if check_database_exists; then
        print_success "Banco 'creditos_db' já existe."
        
        # Verificar se a tabela credito existe
        if check_table_exists; then
            print_success "Tabela 'credito' já existe."
            print_status "Banco de dados está configurado e pronto para uso."
        else
            print_warning "Tabela 'credito' não existe no banco 'creditos_db'."
            request_database_creation_permission
            
            # Executar scripts para criar tabelas e dados
            execute_sql_script "database/02_create_table.sql"
            execute_sql_script "database/03_insert_data.sql"
            execute_sql_script "database/04_cleanup_duplicates.sql"
            execute_sql_script "database/05_add_constraints.sql"
            
            print_success "Tabelas e dados criados com sucesso!"
        fi
    else
        print_warning "Banco 'creditos_db' não existe."
        request_database_creation_permission
        
        # Executar scripts SQL na ordem correta no PostgreSQL local
        execute_sql_script "database/00_create_user.sql"
        execute_sql_script "database/01_create_database.sql"
        execute_sql_script "database/02_create_table.sql"
        execute_sql_script "database/03_insert_data.sql"
        execute_sql_script "database/04_cleanup_duplicates.sql"
        execute_sql_script "database/05_add_constraints.sql"
        
        print_success "Banco de dados configurado com sucesso!"
    fi
    
    echo ""
    print_status "=== INICIANDO SERVIÇOS DOCKER (SEM POSTGRESQL) ==="
    
    # Limpar e iniciar serviços (sem PostgreSQL, pois usamos local)
    cleanup_docker
    start_services
    
    # Aguardar serviços ficarem prontos
    wait_for_services
    
    # Configurar Kafka
    create_kafka_topic
    
    # Verificar status final
    check_final_status
    
    # Mostrar informações de acesso
    show_access_info
}

# Verificar se o script está sendo executado diretamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
