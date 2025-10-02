# Sistema de Créditos - Monorepo

Sistema completo para gerenciamento de créditos constituídos com backend Spring Boot, frontend Angular e banco de dados PostgreSQL.

## Estrutura

```
creditos-monorepo/
├── api/                    # Backend Spring Boot
├── web/                    # Frontend Angular
├── database/               # Scripts do banco de dados
├── infra/                  # Infraestrutura Docker
│   └── docker-compose.yml
└── README.md
```

## Tecnologias

### Backend (api/)
- Spring Boot 3.x
- Java 17
- Maven
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI
- Endpoints: `/api/creditos/{numeroNfse}`, `/actuator/health`, `/api/ping`

### Frontend (web/)
- Angular 17
- TypeScript
- HttpClient para comunicação com API

### Banco de Dados
- PostgreSQL
- Tabela: `credito`

## Como executar

### Execução Local (Recomendado para desenvolvimento)

#### Pré-requisitos
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 18+ (para frontend)

#### 1. Configurar Banco de Dados

```bash
# 1.1. Verificar se PostgreSQL está rodando
brew services start postgresql  # macOS
# ou
sudo systemctl start postgresql  # Linux

# 1.2. Criar usuário postgres (se não existir)
psql -U $(whoami) -d postgres -f database/00_create_user.sql

# 1.3. Executar script de inicialização do banco
./database/init_database.sh
```

#### 2. Configurar Variáveis de Ambiente

```bash
# Exportar variáveis de ambiente para a API
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=creditos_db
export DB_USER=postgres
export DB_PASSWORD=postgres123
```

#### 3. Executar Backend (API)

```bash
# Navegar para o diretório da API
cd api

# Compilar e executar
mvn clean compile
mvn spring-boot:run
```

#### 4. Executar Frontend (Opcional)

```bash
# Em outro terminal, navegar para o frontend
cd web

# Instalar dependências
npm install

# Executar em modo desenvolvimento
ng serve
```

#### 5. Testar a API

```bash
# Teste 1: Buscar créditos por NFS-e existente
curl -X GET "http://localhost:8080/api/creditos/7891011" \
     -H "Accept: application/json"

# Teste 2: Buscar créditos por NFS-e inexistente
curl -X GET "http://localhost:8080/api/creditos/9999999" \
     -H "Accept: application/json"

# Teste 3: Verificar health da aplicação
curl -X GET "http://localhost:8080/actuator/health"

# Teste 4: Endpoint ping
curl -X GET "http://localhost:8080/api/ping"
```

#### 6. Acessar Documentação

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

#### 7. Verificar Dados no Banco

```bash
# Conectar ao banco de dados
psql -h localhost -p 5432 -U postgres -d creditos_db

# Verificar dados na tabela
SELECT * FROM credito;

# Verificar créditos por NFS-e específica
SELECT * FROM credito WHERE numero_nfse = '7891011';
```

### Via Docker Compose (Produção)

1. Navegue até o diretório `infra/`:
```bash
cd infra/
```

2. Execute o docker-compose:
```bash
docker compose up -d --build
```

3. Acesse as aplicações:
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080/api/ping
- **Swagger**: http://localhost:8080/swagger-ui.html

## Dados de Teste

O banco de dados é populado automaticamente com os seguintes dados:

### NFS-e: 7891011 (2 créditos)
- **Crédito 123456**: ISSQN, Simples Nacional, Alíquota 5.0%
- **Crédito 789012**: ISSQN, Não Simples Nacional, Alíquota 4.5%

### NFS-e: 1122334 (1 crédito)
- **Crédito 654321**: Outros, Simples Nacional, Alíquota 3.5%

## Estrutura da API

### Endpoints Disponíveis

- `GET /api/creditos/{numeroNfse}` - Buscar créditos por NFS-e
- `GET /api/ping` - Health check simples
- `GET /actuator/health` - Health check detalhado

### Exemplo de Resposta

```json
[
  {
    "numeroCredito": "123456",
    "numeroNfse": "7891011",
    "dataConstituicao": "2024-02-25",
    "valorIssqn": 1500.75,
    "tipoCredito": "ISSQN",
    "simplesNacional": true,
    "aliquota": 5.0,
    "valorFaturado": 30000.00,
    "valorDeducao": 5000.00,
    "baseCalculo": 25000.00
  }
]
```

## Configuração

### Variáveis de Ambiente

O backend aceita as seguintes variáveis:
- `API_PORT`: Porta da aplicação (default: 8080)
- `ALLOWED_ORIGINS`: Origens permitidas para CORS (default: http://localhost:3000)
- `DB_HOST`: Host do banco de dados (default: localhost)
- `DB_PORT`: Porta do banco de dados (default: 5432)
- `DB_NAME`: Nome do banco de dados (default: creditos_db)
- `DB_USER`: Usuário do banco de dados (default: postgres)
- `DB_PASSWORD`: Senha do banco de dados (default: postgres123)

### Frontend - URL da API

Para configurar a URL da API no frontend, você tem duas opções:

1. **Via variável de ambiente** (recomendado para produção):
   - Edite `web/src/environments/environment.ts`
   - Descomente e configure `apiBaseUrl`

2. **Via arquivo assets/env.json** (carregado em runtime):
   - Crie `web/src/assets/env.json` com:
   ```json
   {
     "apiBaseUrl": "http://localhost:8080"
   }
   ```

## Comandos Úteis

### Desenvolvimento Local

```bash
# Parar a API
Ctrl+C no terminal onde está rodando

# Recompilar API
cd api && mvn clean compile

# Executar testes
cd api && mvn test

# Ver logs da aplicação
tail -f logs/application.log
```

### Docker

```bash
# Parar os serviços
docker compose -f infra/docker-compose.yml down

# Ver logs
docker compose -f infra/docker-compose.yml logs -f

# Rebuild completo
docker compose -f infra/docker-compose.yml up -d --build --force-recreate
```

### Banco de Dados

```bash
# Conectar ao banco
psql -h localhost -p 5432 -U postgres -d creditos_db

# Recriar banco de dados
./database/init_database.sh

# Limpar dados duplicados
./database/fix_duplicates.sh
```

## Troubleshooting

### Problemas Comuns

1. **PostgreSQL não está rodando**:
   ```bash
   brew services start postgresql  # macOS
   sudo systemctl start postgresql  # Linux
   ```

2. **Erro de conexão com banco**:
   - Verifique se as variáveis de ambiente estão configuradas
   - Confirme se o banco de dados foi criado corretamente

3. **Porta já em uso**:
   ```bash
   # Verificar processo na porta 8080
   lsof -i :8080
   
   # Matar processo se necessário
   kill -9 <PID>
   ```

4. **Dependências não encontradas**:
   ```bash
   # Limpar cache do Maven
   cd api && mvn clean
   
   # Reinstalar dependências do frontend
   cd web && rm -rf node_modules && npm install
   ```
