# 🚀 Sistema de Créditos - Execução Completa

Este documento explica como executar o sistema completo de créditos do zero, utilizando apenas Docker e PostgreSQL.

## 📋 Pré-requisitos

Antes de executar o sistema, certifique-se de ter instalado:

- **Docker** (versão 20.10 ou superior)
- **Docker Compose** (versão 2.0 ou superior)
- **PostgreSQL** (versão 12 ou superior)

### Verificação dos Pré-requisitos

```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker-compose --version
# ou
docker compose version

# Verificar PostgreSQL
psql --version
```

## 🎯 Execução Rápida

Para executar o sistema completo, basta executar o script principal:

```bash
./executar_sistema_completo.sh
```

## 📖 O que o Script Faz

O script `executar_sistema_completo.sh` executa as seguintes etapas automaticamente:

### 1. Verificações Iniciais
- ✅ Verifica se PostgreSQL está rodando e acessível
- ✅ Verifica se Docker está instalado e rodando
- ✅ Verifica se Docker Compose está disponível

### 2. Configuração do Banco de Dados
- 🔧 Executa os scripts SQL na ordem correta:
  - `00_create_user.sql` - Cria usuário postgres com permissões
  - `01_create_database.sql` - Cria o banco `creditos_db`
  - `02_create_table.sql` - Cria tabela `credito` com índices e triggers
  - `03_insert_data.sql` - Insere dados iniciais de teste
  - `04_cleanup_duplicates.sql` - Remove possíveis duplicatas
  - `05_add_constraints.sql` - Adiciona constraints de unicidade

### 3. Inicialização dos Serviços Docker
- 🐳 Limpa containers e volumes existentes
- 🧹 Remove containers conflitantes específicos
- 🗑️ Limpa imagens órfãs do Docker
- 🔨 Constrói as imagens Docker (API e Web)
- 🚀 Inicia todos os serviços via Docker Compose:
  - **API** (porta 8080)
  - **Web** (porta 3000)


### 5. Verificações Finais
- 🔍 Verifica se todos os containers estão rodando
- 🧪 Testa todos os endpoints
- 📊 Verifica conectividade com o banco de dados
- 📈 Mostra estatísticas dos dados inseridos

## 🌐 Acessos Após Execução

Após a execução bem-sucedida, você terá acesso aos seguintes serviços:

### Frontend
- **URL**: http://localhost:3000
- **Descrição**: Interface web do sistema de créditos

### API Backend
- **URL**: http://localhost:8080
- **Descrição**: API REST do sistema
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health


### Banco de Dados
- **Host**: localhost:5432
- **Database**: creditos_db
- **Usuário**: postgres
- **Senha**: (conforme configurado no PostgreSQL)

## 🛠️ Comandos Úteis

### Parar o Sistema
```bash
cd infra
docker-compose down
```

### Ver Logs dos Serviços
```bash
cd infra
docker-compose logs -f [servico]

# Exemplos:
docker-compose logs -f api
docker-compose logs -f web
```

### Verificar Status dos Containers
```bash
docker ps
```

### Acessar Container da API
```bash
docker exec -it infra-api-1 /bin/bash
```


## 🔧 Configuração do PostgreSQL

### Configuração Mínima Necessária

O script assume que o PostgreSQL está configurado com:
- Usuário `postgres` com senha configurada
- Acesso local habilitado
- Permissões para criar bancos de dados

### Configuração Manual (se necessário)

Se você precisar configurar o PostgreSQL manualmente:

```bash
# Conectar como superusuário
sudo -u postgres psql

# Criar usuário postgres (se não existir)
CREATE USER postgres WITH PASSWORD 'sua_senha_aqui';
ALTER USER postgres WITH SUPERUSER CREATEDB CREATEROLE;

# Configurar acesso local (no arquivo pg_hba.conf)
# Adicionar linha: local   all             postgres                                trust
```

## 🐛 Solução de Problemas

### Erro: "psql não encontrado"
- Instale o PostgreSQL: `brew install postgresql` (macOS) ou `sudo apt install postgresql` (Ubuntu)
- Adicione o PostgreSQL ao PATH

### Erro: "Não foi possível conectar ao PostgreSQL"
- Verifique se o PostgreSQL está rodando: `brew services start postgresql`
- Verifique se o usuário postgres existe e tem as permissões corretas

### Erro: "Docker não está rodando"
- Inicie o Docker Desktop
- Verifique se o Docker daemon está rodando: `docker info`

### Erro: "Timeout aguardando serviços"
- Verifique se as portas 3000, 8080, 9092 não estão sendo usadas por outros serviços
- Aumente o timeout no script se necessário

### Erro: "Container name already exists" ou "Conflict. The container name is already..."
- ✅ **RESOLVIDO**: O script agora remove automaticamente containers conflitantes
- O script executa: `docker rm -f infra-api-1 infra-web-1` antes de iniciar
- Se ainda ocorrer, execute manualmente: `docker rm -f $(docker ps -aq)`

### Erro: "Connection to localhost:5432 refused" na API
- ✅ **RESOLVIDO**: A API agora se conecta ao PostgreSQL do host usando `host.docker.internal`
- O docker-compose.yml foi configurado com as variáveis de ambiente corretas:
  - `DB_HOST=host.docker.internal`
  - `DB_NAME=creditos_db`
  - `DB_USER=postgres`
  - `DB_PASSWORD=postgres`

### Erro: "Porta já em uso"
- Pare outros serviços que possam estar usando as portas
- Use `lsof -i :PORT` para identificar processos usando a porta

## 📊 Monitoramento

### Health Checks
- API: http://localhost:8080/actuator/health
- Web: http://localhost:3000

### Métricas do Sistema
```bash
# Uso de recursos dos containers
docker stats

# Logs em tempo real
cd infra && docker-compose logs -f
```

## 🔄 Reinicialização

Para reinicializar o sistema completamente:

```bash
# Parar todos os serviços
cd infra && docker-compose down --volumes

# Voltar ao diretório raiz e executar novamente
cd ..
./executar_sistema_completo.sh
```

## 📝 Logs e Debugging

### Logs da API
```bash
cd infra
docker-compose logs -f api
```

```

### Logs do Web
```bash
cd infra
docker-compose logs -f web
```

### Verificar Conectividade com o Banco
```bash
psql -h localhost -U postgres -d creditos_db -c "SELECT COUNT(*) FROM credito;"
```

---

## 🎉 Pronto!

Agora você tem um sistema completo de créditos rodando localmente com:
- ✅ Banco de dados PostgreSQL configurado
- ✅ API Spring Boot
- ✅ Frontend Angular
- ✅ Tudo containerizado e pronto para uso

Para mais informações sobre o sistema, consulte os outros arquivos README no projeto.
