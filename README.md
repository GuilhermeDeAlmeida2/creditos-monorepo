# Sistema de Créditos - Monorepo

Monorepo mínimo com backend Spring Boot e frontend Angular para demonstração de integração.

## Estrutura

```
creditos-monorepo/
├── api/                    # Backend Spring Boot
├── web/                    # Frontend Angular
├── infra/                  # Infraestrutura Docker
│   └── docker-compose.yml
└── README.md
```

## Tecnologias

### Backend (api/)
- Spring Boot 3.x
- Java 17
- Maven
- Endpoints: `/actuator/health`, `/api/ping`

### Frontend (web/)
- Angular 17
- TypeScript
- HttpClient para comunicação com API

## Como executar

### Via Docker Compose (Recomendado)

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

### Teste Manual

1. **Teste direto da API**:
```bash
curl http://localhost:8080/api/ping
```

2. **Teste do frontend**:
- Acesse http://localhost:3000
- Clique no botão "Ping API"
- Verifique se o JSON da resposta é exibido

## Configuração

### Variáveis de Ambiente

O backend aceita as seguintes variáveis:
- `API_PORT`: Porta da aplicação (default: 8080)
- `ALLOWED_ORIGINS`: Origens permitidas para CORS (default: http://localhost:3000)

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

```bash
# Parar os serviços
docker compose -f infra/docker-compose.yml down

# Ver logs
docker compose -f infra/docker-compose.yml logs -f

# Rebuild completo
docker compose -f infra/docker-compose.yml up -d --build --force-recreate
```
