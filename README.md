# Sistema de Créditos - Monorepo

Sistema completo para gerenciamento de créditos constituídos com backend Spring Boot, frontend Angular e banco de dados PostgreSQL.

## 🚀 Execução Rápida (Recomendado)

Para executar o sistema completo de forma automatizada:

```bash
# Tornar o script executável (apenas na primeira vez)
chmod +x executar_sistema_completo.sh

# Executar o sistema completo
./executar_sistema_completo.sh
```

### O que o script faz:
- ✅ Verifica e inicia PostgreSQL automaticamente (macOS/Linux)
- ✅ Configura banco de dados e tabelas
- ✅ Inicia API e Frontend via Docker
- ✅ Testa todos os serviços
- ✅ Mostra URLs de acesso

### Acesso após execução:
- **Frontend**: http://localhost:3000
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Parar o sistema:
```bash
cd infra && docker-compose down
```

---

## 📋 Pré-requisitos

- **Docker** e **Docker Compose**
- **PostgreSQL** local (será iniciado automaticamente se necessário)
- **Java 17+** (apenas para desenvolvimento local)
- **Node.js 18+** (apenas para desenvolvimento local)

## 🏗️ Estrutura do Projeto

```
creditos-monorepo/
├── api/                    # Backend Spring Boot
├── web/                    # Frontend Angular
├── database/               # Scripts do banco de dados
├── infra/                  # Infraestrutura Docker
├── executar_sistema_completo.sh  # Script de execução automatizada
└── README.md
```

## 🛠️ Tecnologias

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

## 🔧 Execução Manual (Desenvolvimento)

### Pré-requisitos
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 18+ (para frontend)

### 1. Configurar Banco de Dados

```bash
# Verificar se PostgreSQL está rodando
brew services start postgresql  # macOS
# ou
sudo systemctl start postgresql  # Linux

# Criar usuário postgres (se não existir)
psql -U $(whoami) -d postgres -f database/00_create_user.sql

# Executar script de inicialização do banco
./database/init_database.sh
```

### 2. Configurar Variáveis de Ambiente

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=creditos_db
export DB_USER=postgres
export DB_PASSWORD=postgres123
```

### 3. Executar Backend (API)

```bash
cd api
mvn clean compile
mvn spring-boot:run
```

### 4. Executar Frontend

```bash
cd web
npm install
ng serve
```

### 5. Testar a API

```bash
# Buscar créditos por NFS-e existente
curl -X GET "http://localhost:8080/api/creditos/7891011" \
     -H "Accept: application/json"

# Verificar health da aplicação
curl -X GET "http://localhost:8080/actuator/health"

# Endpoint ping
curl -X GET "http://localhost:8080/api/ping"
```

### 6. Acessar Documentação

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

### 7. Verificar Dados no Banco

```bash
psql -h localhost -p 5432 -U postgres -d creditos_db
SELECT * FROM credito;
SELECT * FROM credito WHERE numero_nfse = '7891011';
```

## 🐳 Execução via Docker Compose

```bash
cd infra/
docker compose up -d --build
```

**Acesso:**
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080/api/ping
- **Swagger**: http://localhost:8080/swagger-ui.html

## 📊 Dados de Teste

O banco de dados é populado automaticamente com os seguintes dados:

### NFS-e: 7891011 (2 créditos)
- **Crédito 123456**: ISSQN, Simples Nacional, Alíquota 5.0%
- **Crédito 789012**: ISSQN, Não Simples Nacional, Alíquota 4.5%

### NFS-e: 1122334 (1 crédito)
- **Crédito 654321**: Outros, Simples Nacional, Alíquota 3.5%

## 🔌 API Reference

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

## ⚙️ Configuração

### Variáveis de Ambiente (Backend)

- `API_PORT`: Porta da aplicação (default: 8080)
- `ALLOWED_ORIGINS`: Origens permitidas para CORS (default: http://localhost:3000)
- `DB_HOST`: Host do banco de dados (default: localhost)
- `DB_PORT`: Porta do banco de dados (default: 5432)
- `DB_NAME`: Nome do banco de dados (default: creditos_db)
- `DB_USER`: Usuário do banco de dados (default: postgres)
- `DB_PASSWORD`: Senha do banco de dados (default: postgres123)

### Frontend - URL da API

**Opção 1 - Via variável de ambiente** (recomendado para produção):
```typescript
// web/src/environments/environment.ts
export const environment = {
  apiBaseUrl: 'http://localhost:8080'
};
```

**Opção 2 - Via arquivo assets/env.json** (carregado em runtime):
```json
// web/src/assets/env.json
{
  "apiBaseUrl": "http://localhost:8080"
}
```

## 🛠️ Comandos Úteis

### Desenvolvimento Local
```bash
# Recompilar API
cd api && mvn clean compile

# Executar testes
cd api && mvn test

# Reinstalar dependências frontend
cd web && rm -rf node_modules && npm install
```

### Docker
```bash
# Parar serviços
cd infra && docker-compose down

# Ver logs
cd infra && docker-compose logs -f

# Rebuild completo
cd infra && docker-compose up -d --build --force-recreate
```

### Banco de Dados
```bash
# Conectar ao banco
psql -h localhost -p 5432 -U postgres -d creditos_db

# Recriar banco de dados
./database/init_database.sh
```

## 🔧 Troubleshooting

### Problemas Comuns

**PostgreSQL não está rodando:**
```bash
brew services start postgresql  # macOS
sudo systemctl start postgresql  # Linux
```

**Porta já em uso:**
```bash
lsof -i :8080  # Verificar processo na porta 8080
kill -9 <PID>  # Matar processo se necessário
```

**Erro de conexão com banco:**
- Verifique se as variáveis de ambiente estão configuradas
- Confirme se o banco de dados foi criado corretamente

**Dependências não encontradas:**
```bash
cd api && mvn clean  # Limpar cache do Maven
cd web && rm -rf node_modules && npm install  # Reinstalar dependências
```
