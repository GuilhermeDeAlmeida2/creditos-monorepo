# Configuração de Ambientes - Sistema de Créditos

Este documento explica como configurar e executar o sistema em diferentes ambientes (desenvolvimento, homologação e produção).

## 🏗️ Estrutura de Ambientes

### Backend (Spring Boot)
- **Desenvolvimento**: `application-dev.yml`
- **Homologação**: `application-hml.yml`
- **Produção**: `application-prod.yml`

### Frontend (Angular)
- **Desenvolvimento**: `environment.development.ts`
- **Homologação**: `environment.homologation.ts`
- **Produção**: `environment.production.ts`

### Docker Compose
- **Desenvolvimento**: `docker-compose.dev.yml`
- **Homologação**: `docker-compose.hml.yml`
- **Produção**: `docker-compose.prod.yml`

## 🚀 Como Executar

### Ambiente de Desenvolvimento
```bash
# Usando script automatizado
./scripts/start-dev.sh

# Ou manualmente
cd infra
docker-compose -f docker-compose.dev.yml up --build -d
```

**Características:**
- ✅ Funcionalidades de teste habilitadas
- ✅ Swagger habilitado
- ✅ Logs detalhados
- ✅ CORS liberado para localhost

### Ambiente de Homologação
```bash
# Usando script automatizado
./scripts/start-hml.sh

# Ou manualmente
cd infra
docker-compose -f docker-compose.hml.yml up --build -d
```

**Características:**
- ❌ Funcionalidades de teste desabilitadas
- ✅ Swagger habilitado
- ⚠️ Logs reduzidos
- 🔒 CORS restrito para domínios de homologação

### Ambiente de Produção
```bash
# Usando script automatizado
./scripts/start-prod.sh

# Ou manualmente
cd infra
docker-compose -f docker-compose.prod.yml up --build -d
```

**Características:**
- ❌ Funcionalidades de teste desabilitadas
- ❌ Swagger desabilitado
- ⚠️ Logs mínimos
- 🔒 CORS restrito para domínios de produção

## 🔧 Funcionalidades de Teste

As funcionalidades de teste estão disponíveis apenas no ambiente de desenvolvimento:

### Backend
- `POST /api/creditos/teste/gerar` - Gerar registros de teste
- `DELETE /api/creditos/teste/deletar` - Deletar registros de teste

### Frontend
- Botão "Gerar Registros de Teste"
- Botão "Deletar Registros de Teste"

### Controle de Acesso
- **Desenvolvimento**: Funcionalidades habilitadas
- **Homologação/Produção**: Retorna erro 403 (Forbidden)

## 📊 Configurações por Ambiente

### Desenvolvimento
```yaml
app:
  environment: development
  test-features:
    enabled: true
```

### Homologação
```yaml
app:
  environment: homologation
  test-features:
    enabled: false
```

### Produção
```yaml
app:
  environment: production
  test-features:
    enabled: false
```

## 🌐 URLs de Acesso

### Desenvolvimento
- **API**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Swagger**: http://localhost:8080/swagger-ui.html

### Homologação
- **API**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Swagger**: http://localhost:8080/swagger-ui.html

### Produção
- **API**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **Swagger**: ❌ Desabilitado

## 🔍 Verificação de Ambiente

### Backend
Para verificar o ambiente ativo, acesse:
```
GET /actuator/info
```

### Frontend
O ambiente é definido no build e pode ser verificado no console do navegador:
```typescript
console.log(environment.environment);
console.log(environment.testFeatures.enabled);
```

## 🛠️ Desenvolvimento Local

Para desenvolvimento local sem Docker:

### Backend
```bash
cd api
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Frontend
```bash
cd web
ng serve --configuration=development
```

## 📝 Notas Importantes

1. **Segurança**: As funcionalidades de teste são automaticamente desabilitadas em ambientes não-desenvolvimento
2. **Logs**: O nível de log é ajustado automaticamente por ambiente
3. **CORS**: As origens permitidas são configuradas por ambiente
4. **Banco de Dados**: Cada ambiente pode usar um banco diferente
5. **Kafka**: Os tópicos são prefixados por ambiente para evitar conflitos

## 🚨 Troubleshooting

### Problema: Funcionalidades de teste não aparecem
**Solução**: Verifique se está executando em ambiente de desenvolvimento

### Problema: Erro 403 nas funcionalidades de teste
**Solução**: Normal em ambientes de homologação/produção

### Problema: CORS error
**Solução**: Verifique se a origem está configurada no ambiente correto
