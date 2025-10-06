# 📊 Plano de Aumento da Cobertura de Testes

## 🎯 Objetivo
Aumentar a cobertura de testes do backend para pelo menos **85%** em todos os arquivos, priorizando os arquivos com menor cobertura atual.

## 📈 Cobertura Atual
- **Cobertura Geral**: 59% (instruções)
- **Classes**: 97% (57 de 59)
- **Métodos**: 77% (467 de 653)
- **Linhas**: 64% (1.439 de 2.239)

## 🔥 Prioridades (Menor para Maior Cobertura)

### 1. **CRÍTICO** - 0% a 10% de cobertura:
- [ ] `CommandValidationException` (0%)
- [ ] `CommandFactory` (3%)
- [ ] `ValidationFactory` (3%)
- [ ] `DeleteTestDataCommand` (3%)
- [ ] `GenerateTestDataCommand` (7%)

### 2. **ALTO** - 10% a 30% de cobertura:
- [ ] `CommandResult` (27%)
- [ ] `ValidationException` (12%)
- [ ] `ValidationService` (30%)

### 3. **MÉDIO** - 30% a 50% de cobertura:
- [ ] `TaxCalculationService` (40%)
- [ ] `CommandStatus` (43%)
- [ ] `PageableValidationHandler` (44%)

### 4. **BAIXO** - 50% a 85% de cobertura:
- [ ] Outros arquivos que não atingem 85%

## 📋 Estratégia de Implementação

### Fase 1: Arquivos Críticos (0-10%)
1. **CommandValidationException**: Criar testes para todos os construtores e métodos
2. **CommandFactory**: Testar criação de comandos e tratamento de exceções
3. **ValidationFactory**: Testar criação de validadores e cenários de erro
4. **DeleteTestDataCommand**: Testar execução e validações
5. **GenerateTestDataCommand**: Testar geração de dados e cenários edge case

### Fase 2: Arquivos de Alta Prioridade (10-30%)
1. **CommandResult**: Testar todos os builders e métodos de resultado
2. **ValidationException**: Testar construtores e métodos de exceção
3. **ValidationService**: Testar todas as estratégias de validação

### Fase 3: Arquivos de Média Prioridade (30-50%)
1. **TaxCalculationService**: Testar cálculos de taxa e cenários edge case
2. **CommandStatus**: Testar todos os estados e transições
3. **PageableValidationHandler**: Testar validações de paginação

### Fase 4: Finalização (50-85%)
1. Revisar arquivos restantes
2. Adicionar testes para cenários não cobertos
3. Verificar cobertura final

## 🛠️ Diretrizes Técnicas

### Estrutura dos Testes:
```java
@ExtendWith(MockitoExtension.class)
class NomeClasseTest {
    
    @Mock
    private Dependencia dependencia;
    
    @InjectMocks
    private NomeClasse nomeClasse;
    
    @Test
    @DisplayName("Deve testar cenário específico")
    void deveTestarCenarioEspecifico() {
        // Given
        // When
        // Then
    }
}
```

### Tipos de Teste a Implementar:
1. **Testes Unitários**: Métodos individuais
2. **Testes de Construtor**: Todos os construtores
3. **Testes de Exceção**: Cenários de erro
4. **Testes de Edge Cases**: Valores limite

### Convenções:
- Usar `@DisplayName` para descrições em português
- Seguir padrão Given-When-Then
- Mockar todas as dependências externas
- Não executar testes de integração (poupar tempo)
- Focar em cenários de sucesso e falha

## 📊 Métricas de Sucesso
- [ ] Todos os arquivos com ≥ 85% de cobertura
- [ ] Cobertura geral ≥ 85%
- [ ] Todos os métodos públicos testados
- [ ] Cenários de exceção cobertos
- [ ] Edge cases testados

## 🚀 Comandos de Execução
```bash
# Executar testes unitários (sem integração)
mvn test -Dtest="*Test" -DfailIfNoTests=false

# Gerar relatório de cobertura
mvn jacoco:report

# Verificar cobertura específica
mvn jacoco:check -Djacoco.check.line=85
```

## 📝 Log de Progresso
- [x] Análise inicial da cobertura
- [x] Criação do plano de ação
- [ ] Implementação dos testes críticos
- [ ] Implementação dos testes de alta prioridade
- [ ] Implementação dos testes de média prioridade
- [ ] Finalização e verificação

---
**Data de Criação**: $(date)
**Responsável**: Guilherme de Almeida Freitas
**Status**: Em Execução
