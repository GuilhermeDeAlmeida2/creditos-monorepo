# Scripts de Banco de Dados

Este diretório contém os scripts necessários para configurar o banco de dados da aplicação de créditos.

## Arquivos

- `00_create_user.sql` - Script para criação do usuário postgres com permissões adequadas
- `01_create_database.sql` - Script para criação do banco de dados
- `02_create_table.sql` - Script para criação da tabela `credito`
- `03_insert_data.sql` - Script para inserção dos dados iniciais (com verificação de duplicados)
- `04_cleanup_duplicates.sql` - Script para limpeza de registros duplicados
- `05_add_constraints.sql` - Script para adicionar constraints de unicidade
- `init_database.sh` - Script de inicialização automática
- `fix_duplicates.sh` - Script para corrigir registros duplicados

## Pré-requisitos

1. **PostgreSQL instalado** na sua máquina
2. **Usuário postgres** configurado com permissões adequadas
3. **PostgreSQL rodando** como serviço

### Configuração do Usuário Postgres

Antes de executar os scripts, você precisa criar o usuário `postgres` com as permissões adequadas:

```bash
# Execute como superusuário (seu usuário atual)
psql -U $(whoami) -d postgres -f database/00_create_user.sql
```

Este script irá:
- Criar o usuário `postgres` com senha `postgres123`
- Conceder permissões de superusuário
- Permitir criação de bancos de dados
- Permitir criação de usuários
- Permitir replicação

### Instalação do PostgreSQL

#### macOS (usando Homebrew)
```bash
brew install postgresql
brew services start postgresql
```

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

#### CentOS/RHEL
```bash
sudo yum install postgresql-server postgresql-contrib
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

## Como usar

### ⚠️ IMPORTANTE: Use o script automático primeiro

**SEMPRE execute o script automático `init_database.sh` antes de tentar executar os scripts manualmente.** O script automático é mais seguro, verifica dependências e orienta você através do processo.

### Resumo rápido (para usuários experientes)

```bash
# 1. Criar usuário postgres
psql -U $(whoami) -d postgres -f database/00_create_user.sql

# 2. Executar script automático
./database/init_database.sh

# 3. (Opcional) Corrigir duplicados se necessário
./database/fix_duplicates.sh
```

### Passo 1: Criar usuário postgres

Primeiro, execute o script para criar o usuário postgres:

```bash
# Criar usuário postgres
psql -U $(whoami) -d postgres -f database/00_create_user.sql
```

### Passo 2: Executar script automático (Recomendado)

Execute o script de inicialização a partir do diretório raiz do projeto:

```bash
./database/init_database.sh
```

Este script irá:
1. Verificar se o PostgreSQL está rodando
2. Verificar se o usuário postgres existe (e orientar a criação se necessário)
3. Criar o banco de dados `creditos_db` (se não existir)
4. Verificar se a tabela `credito` existe e perguntar se deseja limpar dados existentes
5. Criar a tabela `credito` com todas as colunas e índices (se não existir)
6. Inserir os dados iniciais (apenas se a tabela foi limpa ou está vazia)
7. Configurar triggers para atualização automática de timestamps
8. Adicionar constraints de unicidade

### ⚠️ Execução manual (Apenas se necessário)

**Use esta opção APENAS se o script automático falhar ou se você precisar de controle específico sobre cada etapa.**

Se preferir executar os scripts manualmente:

```bash
# 1. Criar usuário postgres (se ainda não foi criado)
psql -U $(whoami) -d postgres -f database/00_create_user.sql

# 2. Criar o banco de dados
psql -U postgres -f database/01_create_database.sql

# 3. Criar a tabela
psql -U postgres -d creditos_db -f database/02_create_table.sql

# 4. Inserir dados iniciais
psql -U postgres -d creditos_db -f database/03_insert_data.sql

# 5. Adicionar constraints de unicidade
psql -U postgres -d creditos_db -f database/05_add_constraints.sql
```

### Passo 3: Correção de duplicados (se necessário)

Se você já tem registros duplicados no banco:

```bash
./database/fix_duplicates.sh
```

## Estrutura da Tabela

A tabela `credito` possui as seguintes colunas:

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | BIGINT | Identificador único (auto-incremento) |
| numero_credito | VARCHAR(50) | Número do crédito |
| numero_nfse | VARCHAR(50) | Número da NFSe |
| data_constituicao | DATE | Data de constituição do crédito |
| valor_issqn | DECIMAL(15,2) | Valor do ISSQN |
| tipo_credito | VARCHAR(50) | Tipo do crédito (ISSQN, Outros, etc.) |
| simples_nacional | BOOLEAN | Indica se é do Simples Nacional |
| aliquota | DECIMAL(5,2) | Alíquota aplicada |
| valor_faturado | DECIMAL(15,2) | Valor faturado |
| valor_deducao | DECIMAL(15,2) | Valor da dedução |
| base_calculo | DECIMAL(15,2) | Base de cálculo |
| created_at | TIMESTAMP | Data de criação (automático) |
| updated_at | TIMESTAMP | Data de atualização (automático) |

## Dados Iniciais

O script insere 3 registros de exemplo:

1. Crédito 123456 - ISSQN, Simples Nacional, 5% de alíquota
2. Crédito 789012 - ISSQN, não Simples Nacional, 4.5% de alíquota  
3. Crédito 654321 - Outros, Simples Nacional, 3.5% de alíquota

## Conexão com o Banco

Para conectar ao banco de dados:

```bash
psql -U postgres -d creditos_db
```

**Senha do usuário postgres**: `postgres123`

## Problemas com Duplicados

### Por que ocorrem duplicados?

1. **Execução múltipla do script de inserção** - O script `03_insert_data.sql` foi executado várias vezes
2. **Falta de constraints de unicidade** - A tabela não tinha proteção contra duplicados
3. **Script de inicialização sem verificação** - Não verificava se dados já existiam

### Como corrigir duplicados existentes?

Execute o script de correção:

```bash
./database/fix_duplicates.sh
```

Este script irá:
1. Identificar registros duplicados (baseado em `numero_credito + numero_nfse + data_constituicao + tipo_credito`)
2. Remover duplicados (mantendo apenas o primeiro de cada grupo)
3. Adicionar constraints de unicidade
4. Mostrar estatísticas antes e depois da correção

### Prevenção de duplicados futuros

Os scripts agora incluem:
- **Verificação de dados existentes** no script de inserção
- **Constraints de unicidade** para `numero_credito` e combinação de campos (`numero_credito + numero_nfse + data_constituicao + tipo_credito`)
- **Proteção no script de inicialização** contra execuções múltiplas

## Troubleshooting

### PostgreSQL não está rodando
```bash
# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql
```

### Erro de permissão
Se você encontrar erros de permissão, certifique-se de que o usuário `postgres` foi criado corretamente:

```bash
# Verificar se o usuário postgres existe
psql -U $(whoami) -d postgres -c "SELECT rolname FROM pg_roles WHERE rolname = 'postgres';"

# Se não existir, criar o usuário
psql -U $(whoami) -d postgres -f database/00_create_user.sql
```

### Banco já existe
O script perguntará se você deseja recriar o banco. Responda `y` para recriar ou `N` para manter o existente.

### Tabela com dados existentes
Se a tabela `credito` já existir e tiver dados, o script perguntará se você deseja limpar a tabela antes de continuar. Responda `y` para limpar (remover todos os dados) ou `N` para manter os dados existentes.

## Recursos Adicionais

- **Índices**: Criados automaticamente para melhor performance
- **Triggers**: Atualização automática do campo `updated_at`
- **Comentários**: Documentação das colunas no banco
- **Validações**: Campos obrigatórios e tipos de dados apropriados

