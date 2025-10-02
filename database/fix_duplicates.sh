#!/bin/bash

# Script para corrigir registros duplicados no banco de dados
# Este script remove duplicados e adiciona constraints para prevenir futuras duplicações

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
echo -e "${BLUE}  Correção de Registros Duplicados      ${NC}"
echo -e "${BLUE}========================================${NC}"

# Função para verificar se o PostgreSQL está rodando
check_postgres() {
    echo -e "${YELLOW}Verificando se o PostgreSQL está rodando...${NC}"
    
    if ! pg_isready -h $DB_HOST -p $DB_PORT > /dev/null 2>&1; then
        echo -e "${RED}Erro: PostgreSQL não está rodando ou não está acessível${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}PostgreSQL está rodando!${NC}"
}

# Função para verificar se o banco existe
check_database_exists() {
    echo -e "${YELLOW}Verificando se o banco de dados existe...${NC}"
    
    if ! psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d postgres -lqt | cut -d \| -f 1 | grep -qw $DB_NAME; then
        echo -e "${RED}Erro: Banco de dados '$DB_NAME' não existe${NC}"
        echo -e "${YELLOW}Execute primeiro o script de inicialização: ./database/init_database.sh${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}Banco de dados encontrado!${NC}"
}

# Função para executar scripts SQL
execute_sql_script() {
    local script_file=$1
    local description=$2
    
    echo -e "${YELLOW}Executando: $description${NC}"
    
    if [ -f "$script_file" ]; then
        if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$script_file"; then
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

# Função para mostrar estatísticas
show_statistics() {
    echo -e "${BLUE}Estatísticas do banco de dados:${NC}"
    
    # Total de registros
    local total=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "SELECT COUNT(*) FROM credito;")
    echo -e "  Total de registros: ${GREEN}$total${NC}"
    
    # Registros duplicados
    local duplicados=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "
        SELECT COUNT(*) FROM (
            SELECT numero_credito, numero_nfse, data_constituicao, tipo_credito, COUNT(*) 
            FROM credito 
            GROUP BY numero_credito, numero_nfse, data_constituicao, tipo_credito 
            HAVING COUNT(*) > 1
        ) as dup;")
    
    if [ "$duplicados" -gt 0 ]; then
        echo -e "  Registros duplicados: ${RED}$duplicados${NC}"
    else
        echo -e "  Registros duplicados: ${GREEN}0${NC}"
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
    
    # Verificar se o banco existe
    check_database_exists
    
    # Mostrar estatísticas antes da correção
    echo -e "${YELLOW}Estatísticas ANTES da correção:${NC}"
    show_statistics
    
    # Perguntar se deseja continuar
    echo -e "${YELLOW}Deseja continuar com a correção dos duplicados?${NC}"
    read -p "Isso irá remover registros duplicados (mantendo apenas o primeiro de cada grupo). (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${YELLOW}Operação cancelada pelo usuário.${NC}"
        exit 0
    fi
    
    # Limpar duplicados
    execute_sql_script "database/04_cleanup_duplicates.sql" "Limpeza de registros duplicados"
    
    # Adicionar constraints (se não existirem)
    echo -e "${YELLOW}Verificando constraints de unicidade...${NC}"
    local constraints_exist=$(psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -t -c "
        SELECT COUNT(*) FROM pg_constraint 
        WHERE conrelid = 'credito'::regclass 
        AND contype = 'u';")
    
    if [ "$constraints_exist" -eq 0 ]; then
        execute_sql_script "database/05_add_constraints.sql" "Adição de constraints de unicidade"
    else
        echo -e "${GREEN}✓ Constraints de unicidade já existem!${NC}"
    fi
    
    # Mostrar estatísticas após a correção
    echo -e "${YELLOW}Estatísticas APÓS a correção:${NC}"
    show_statistics
    
    echo -e "${GREEN}========================================${NC}"
    echo -e "${GREEN}  Correção de duplicados concluída!    ${NC}"
    echo -e "${GREEN}========================================${NC}"
    echo -e "${BLUE}O banco de dados agora está limpo e protegido contra duplicados.${NC}"
}

# Executar função principal
main "$@"
