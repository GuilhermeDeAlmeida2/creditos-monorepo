#!/bin/bash

# Script de inicialização do banco de dados
# Este script cria o banco de dados, tabelas e insere dados iniciais

set -e  # Parar em caso de erro

# Configurações
DB_NAME="creditos_db"
DB_USER="postgres"
DB_HOST="localhost"
DB_PORT="5432"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Inicialização do Banco de Dados      ${NC}"
echo -e "${BLUE}========================================${NC}"

# Função para verificar se o PostgreSQL está rodando
check_postgres() {
    echo -e "${YELLOW}Verificando se o PostgreSQL está rodando...${NC}"
    
    if ! pg_isready -h $DB_HOST -p $DB_PORT > /dev/null 2>&1; then
        echo -e "${RED}Erro: PostgreSQL não está rodando ou não está acessível${NC}"
        echo -e "${YELLOW}Certifique-se de que o PostgreSQL está instalado e rodando${NC}"
        echo -e "${YELLOW}Para iniciar o PostgreSQL no macOS:${NC}"
        echo -e "${YELLOW}  brew services start postgresql${NC}"
        echo -e "${YELLOW}Para iniciar o PostgreSQL no Linux:${NC}"
        echo -e "${YELLOW}  sudo systemctl start postgresql${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}PostgreSQL está rodando!${NC}"
}

# Função para verificar se o banco já existe
check_database_exists() {
    echo -e "${YELLOW}Verificando se o banco de dados já existe...${NC}"
    
    if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
        echo -e "${YELLOW}Banco de dados '$DB_NAME' já existe.${NC}"
        read -p "Deseja recriar o banco? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo -e "${YELLOW}Removendo banco de dados existente...${NC}"
            psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "DROP DATABASE IF EXISTS $DB_NAME;"
            echo -e "${GREEN}Banco de dados removido!${NC}"
        else
            echo -e "${YELLOW}Pulando criação do banco de dados...${NC}"
            return 1
        fi
    fi
    return 0
}

# Função para verificar se a tabela existe e tem dados
check_table_exists() {
    echo -e "${YELLOW}Verificando se a tabela credito existe...${NC}"
    
    # Verificar se a tabela existe
    local table_exists=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "
        SELECT COUNT(*) FROM information_schema.tables 
        WHERE table_schema = 'public' 
        AND table_name = 'credito';")
    
    if [ "$table_exists" -gt 0 ]; then
        echo -e "${YELLOW}Tabela 'credito' já existe.${NC}"
        
        # Verificar se tem dados
        local data_count=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM credito;")
        
        if [ "$data_count" -gt 0 ]; then
            echo -e "${YELLOW}Tabela contém $data_count registros.${NC}"
            echo -e "${YELLOW}Deseja limpar a tabela antes de continuar?${NC}"
            echo -e "${YELLOW}Isso irá remover todos os dados existentes.${NC}"
            read -p "Limpar tabela? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                echo -e "${YELLOW}Limpando tabela credito...${NC}"
                psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "TRUNCATE TABLE credito RESTART IDENTITY CASCADE;"
                echo -e "${GREEN}Tabela limpa com sucesso!${NC}"
                return 0
            else
                echo -e "${YELLOW}Mantendo dados existentes.${NC}"
                return 1
            fi
        else
            echo -e "${GREEN}Tabela existe mas está vazia.${NC}"
            return 0
        fi
    else
        echo -e "${GREEN}Tabela não existe. Será criada.${NC}"
        return 0
    fi
}

# Função para executar scripts SQL
execute_sql_script() {
    local script_file=$1
    local description=$2
    local database_name=${3:-$DB_NAME}
    
    echo -e "${YELLOW}Executando: $description${NC}"
    
    if [ -f "$script_file" ]; then
        if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $database_name -f "$script_file"; then
            echo -e "${GREEN}✓ $description executado com sucesso!${NC}"
        else
            echo -e "${RED}✗ Erro ao executar: $description${NC}"
            exit 1
        fi
    else
        echo -e "${RED}✗ Arquivo não encontrado: $script_file${NC}"
        exit 1
    fi
}

# Função principal
main() {
    # Verificar se estamos no diretório correto
    if [ ! -d "database" ]; then
        echo -e "${RED}Erro: Execute este script a partir do diretório raiz do projeto${NC}"
        exit 1
    fi
    
    # Verificar PostgreSQL
    check_postgres
    
    # Criar usuário postgres se necessário
    echo -e "${YELLOW}Verificando se o usuário postgres existe...${NC}"
    if ! psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -c "SELECT 1;" > /dev/null 2>&1; then
        echo -e "${YELLOW}Usuário postgres não encontrado. Criando usuário...${NC}"
        echo -e "${YELLOW}Você precisará executar como superusuário para criar o usuário postgres.${NC}"
        echo -e "${YELLOW}Execute: psql -U $(whoami) -d postgres -f database/00_create_user.sql${NC}"
        read -p "Pressione Enter após executar o comando acima..."
    else
        echo -e "${GREEN}Usuário postgres encontrado!${NC}"
    fi
    
    # Verificar se o banco existe
    if check_database_exists; then
        # Criar banco de dados
        execute_sql_script "database/01_create_database.sql" "Criação do banco de dados" "postgres"
    fi
    
    # Verificar se a tabela existe e se deve ser limpa
    should_insert_data=true
    if check_table_exists; then
        # Tabela foi limpa ou não existia, continuar com inserção
        should_insert_data=true
    else
        # Tabela tem dados e usuário optou por manter
        should_insert_data=false
        echo -e "${YELLOW}Pulando inserção de dados iniciais (dados existentes mantidos).${NC}"
    fi
    
    # Criar tabelas (se não existirem)
    execute_sql_script "database/02_create_table.sql" "Criação da tabela credito"
    
    # Inserir dados iniciais (apenas se tabela foi limpa ou não tinha dados)
    if [ "$should_insert_data" = true ]; then
        execute_sql_script "database/03_insert_data.sql" "Inserção de dados iniciais"
    fi
    
    # Adicionar constraints de unicidade
    execute_sql_script "database/05_add_constraints.sql" "Adição de constraints de unicidade"
    
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Banco de dados inicializado com sucesso!${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo -e "${BLUE}Informações de conexão:${NC}"
    echo -e "  Host: $DB_HOST"
    echo -e "  Porta: $DB_PORT"
    echo -e "  Banco: $DB_NAME"
    echo -e "  Usuário: $DB_USER"
    echo -e ""
    echo -e "${BLUE}Para conectar ao banco:${NC}"
    echo -e "  psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
}

# Executar função principal
main "$@"

