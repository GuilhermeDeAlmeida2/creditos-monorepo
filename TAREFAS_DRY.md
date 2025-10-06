# Lista de Tarefas para Melhorar o Princípio DRY

## 🎯 Objetivo
Elevar a nota do princípio DRY de **6/10** para **9/10** através da eliminação de duplicações de código e criação de utilitários reutilizáveis.

---

## 📋 Tarefas Prioritárias

### 🔥 **ALTA PRIORIDADE** - Duplicações Críticas

#### 1. **Centralizar Constantes de Validação**
**Problema**: `VALID_SORT_FIELDS` duplicado em 3 locais
**Impacto**: Alto - Violação clara do DRY

**Arquivos Afetados:**
- `ValidationService.java` (linha 28)
- `PageableValidationHandler.java` (linha 25)
- `PageableValidationStrategy.java` (linha 24)

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/constants/ValidationConstants.java
public class ValidationConstants {
    public static final Set<String> VALID_SORT_FIELDS = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
    
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_FIELD = "id";
    public static final String DEFAULT_SORT_DIRECTION = "ASC";
}
```

**Tarefas:**
- [ ] Criar classe `ValidationConstants`
- [ ] Substituir todas as ocorrências de `VALID_SORT_FIELDS` duplicadas
- [ ] Atualizar imports nos arquivos afetados
- [ ] Executar testes para garantir que não quebrou nada

---

#### 2. **Extrair Utilitários de Parsing**
**Problema**: Lógica de conversão de tipos repetida em múltiplos handlers
**Impacto**: Alto - Código duplicado em validações

**Arquivos Afetados:**
- `PageableValidationHandler.java` (linhas 79-89, 100-110)
- `NumberValidationHandler.java` (lógica similar)
- Outros handlers com parsing similar

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/util/ValidationUtils.java
public class ValidationUtils {
    
    public static int parseInteger(Object value, String fieldName) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("Parâmetro '%s' deve ser um número inteiro", fieldName)
                );
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Parâmetro '%s' deve ser um número", fieldName)
            );
        }
    }
    
    public static String parseString(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(
                String.format("Parâmetro '%s' deve ser uma string", fieldName)
            );
        }
        return ((String) value).trim();
    }
    
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
```

**Tarefas:**
- [ ] Criar classe `ValidationUtils`
- [ ] Extrair lógica de parsing de `PageableValidationHandler`
- [ ] Extrair lógica de parsing de `NumberValidationHandler`
- [ ] Refatorar outros handlers que usam parsing similar
- [ ] Atualizar testes para usar os novos utilitários

---

#### 3. **Criar Builder para ValidationResult**
**Problema**: Criação de `ValidationResult` repetida com padrões similares
**Impacto**: Médio - Melhora legibilidade e reduz duplicação

**Solução:**
```java
// Adicionar ao ValidationResult.java
public class ValidationResult {
    // ... código existente ...
    
    public static class Builder {
        private boolean valid;
        private String message;
        private String fieldName;
        private Object processedValue;
        
        public Builder valid(boolean valid) {
            this.valid = valid;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        public Builder processedValue(Object value) {
            this.processedValue = value;
            return this;
        }
        
        public ValidationResult build() {
            return new ValidationResult(valid, message, fieldName, processedValue);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
```

**Tarefas:**
- [ ] Adicionar Builder pattern ao `ValidationResult`
- [ ] Refatorar handlers para usar o builder
- [ ] Simplificar criação de resultados de validação

---

### 🔶 **MÉDIA PRIORIDADE** - Melhorias de Estrutura

#### 4. **Extrair Factory para Pageable**
**Problema**: Lógica de criação de `Pageable` complexa e repetitiva
**Impacto**: Médio - Simplifica validação de paginação

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/factory/PageableFactory.java
@Component
public class PageableFactory {
    
    public Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        // Validar e corrigir página
        int validPage = Math.max(0, page);
        
        // Validar e corrigir tamanho
        int validSize = Math.max(1, Math.min(size, ValidationConstants.MAX_PAGE_SIZE));
        if (validSize <= 0) {
            validSize = ValidationConstants.DEFAULT_PAGE_SIZE;
        }
        
        // Validar campo de ordenação
        String validSortBy = ValidationConstants.VALID_SORT_FIELDS.contains(sortBy) 
            ? sortBy 
            : ValidationConstants.DEFAULT_SORT_FIELD;
        
        // Validar direção de ordenação
        Sort.Direction validDirection = "DESC".equalsIgnoreCase(sortDirection) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        Sort sort = Sort.by(validDirection, validSortBy);
        return PageRequest.of(validPage, validSize, sort);
    }
}
```

**Tarefas:**
- [ ] Criar `PageableFactory`
- [ ] Refatorar `PageableValidationHandler` para usar a factory
- [ ] Simplificar lógica de validação de paginação
- [ ] Atualizar testes

---

#### 5. **Criar Enum para Tipos de Validação**
**Problema**: Strings hardcoded para tipos de validação
**Impacto**: Médio - Melhora type safety e reduz erros

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/validation/ValidationType.java
public enum ValidationType {
    STRING_NOT_EMPTY("string.not.empty"),
    STRING_OPTIONAL("string.optional"),
    NUMBER_POSITIVE("number.positive"),
    NUMBER_RANGE("number.range"),
    PAGEABLE("pageable"),
    SORT_FIELD("sort.field"),
    SORT_DIRECTION("sort.direction");
    
    private final String code;
    
    ValidationType(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}
```

**Tarefas:**
- [ ] Criar enum `ValidationType`
- [ ] Refatorar `ValidationRequest` para usar enum
- [ ] Atualizar todos os handlers para usar enum
- [ ] Atualizar testes

---

#### 6. **Consolidar Mensagens de Erro**
**Problema**: Mensagens de erro espalhadas e algumas duplicadas
**Impacto**: Médio - Melhora consistência e manutenibilidade

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/constants/ErrorMessages.java
public class ErrorMessages {
    
    // Validação de strings
    public static final String STRING_CANNOT_BE_EMPTY = "Campo '%s' não pode estar vazio";
    public static final String STRING_MUST_BE_STRING = "Campo '%s' deve ser uma string";
    
    // Validação de números
    public static final String NUMBER_MUST_BE_POSITIVE = "Campo '%s' deve ser um número positivo";
    public static final String NUMBER_MUST_BE_INTEGER = "Campo '%s' deve ser um número inteiro";
    public static final String NUMBER_OUT_OF_RANGE = "Campo '%s' deve estar entre %d e %d";
    
    // Validação de paginação
    public static final String INVALID_SORT_FIELD = "Campo de ordenação '%s' não é válido. Campos válidos: %s";
    public static final String INVALID_SORT_DIRECTION = "Direção de ordenação deve ser 'ASC' ou 'DESC'";
    
    // Métodos utilitários
    public static String formatString(String template, String... args) {
        return String.format(template, (Object[]) args);
    }
}
```

**Tarefas:**
- [ ] Criar classe `ErrorMessages`
- [ ] Consolidar mensagens de erro dos handlers
- [ ] Refatorar handlers para usar mensagens centralizadas
- [ ] Atualizar testes

---

### 🔵 **BAIXA PRIORIDADE** - Refinamentos

#### 7. **Criar Interface para Handlers Específicos**
**Problema**: Alguns handlers têm métodos similares que poderiam ser padronizados
**Impacto**: Baixo - Melhora consistência

**Solução:**
```java
// Criar interfaces específicas
public interface StringValidationHandler extends ValidationHandler {
    ValidationResult validateNotEmpty(String value, String fieldName);
    ValidationResult validateOptional(String value, String fieldName);
}

public interface NumberValidationHandler extends ValidationHandler {
    ValidationResult validatePositive(Number value, String fieldName);
    ValidationResult validateRange(Number value, String fieldName, int min, int max);
}
```

**Tarefas:**
- [ ] Criar interfaces específicas para handlers
- [ ] Refatorar implementações para implementar interfaces
- [ ] Atualizar testes

---

#### 8. **Extrair Configurações de Validação**
**Problema**: Configurações hardcoded espalhadas pelo código
**Impacto**: Baixo - Melhora configurabilidade

**Solução:**
```java
// Criar: src/main/java/br/com/guilhermedealmeidafreitas/creditos/config/ValidationConfig.java
@ConfigurationProperties(prefix = "app.validation")
@Data
public class ValidationConfig {
    private int defaultPageSize = 10;
    private int maxPageSize = 100;
    private String defaultSortField = "id";
    private String defaultSortDirection = "ASC";
    private Set<String> validSortFields = Set.of(
        "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
        "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
        "valorFaturado", "valorDeducao", "baseCalculo"
    );
}
```

**Tarefas:**
- [ ] Criar `ValidationConfig`
- [ ] Mover configurações hardcoded para properties
- [ ] Atualizar constantes para usar configuração
- [ ] Adicionar propriedades no `application.yml`

---

## 📊 Cronograma de Implementação

### **Semana 1** - Alta Prioridade
- [ ] Tarefa 1: Centralizar Constantes de Validação
- [ ] Tarefa 2: Extrair Utilitários de Parsing
- [ ] Tarefa 3: Criar Builder para ValidationResult

### **Semana 2** - Média Prioridade
- [ ] Tarefa 4: Extrair Factory para Pageable
- [ ] Tarefa 5: Criar Enum para Tipos de Validação
- [ ] Tarefa 6: Consolidar Mensagens de Erro

### **Semana 3** - Baixa Prioridade
- [ ] Tarefa 7: Criar Interface para Handlers Específicos
- [ ] Tarefa 8: Extrair Configurações de Validação

---

## 🧪 Estratégia de Testes

Para cada tarefa, seguir o seguinte processo:

1. **Testes Antes da Refatoração**
   - [ ] Executar todos os testes existentes
   - [ ] Documentar comportamento atual

2. **Implementação**
   - [ ] Implementar mudanças seguindo TDD quando possível
   - [ ] Manter compatibilidade com código existente

3. **Testes Após Refatoração**
   - [ ] Executar todos os testes existentes
   - [ ] Adicionar novos testes para utilitários criados
   - [ ] Verificar cobertura de código

4. **Validação**
   - [ ] Executar testes de integração
   - [ ] Testar endpoints manualmente
   - [ ] Verificar que não há regressões

---

## 📈 Métricas de Sucesso

### **Antes da Refatoração (Nota: 6/10)**
- ❌ 3 duplicações de `VALID_SORT_FIELDS`
- ❌ Lógica de parsing duplicada em 3+ locais
- ❌ Mensagens de erro inconsistentes
- ❌ Configurações hardcoded espalhadas

### **Após a Refatoração (Meta: 9/10)**
- ✅ Constantes centralizadas em 1 local
- ✅ Utilitários de parsing reutilizáveis
- ✅ Mensagens de erro padronizadas
- ✅ Configurações externalizadas
- ✅ Código mais limpo e manutenível
- ✅ Melhor testabilidade

---

## 🎯 Critérios de Aceitação

Uma tarefa será considerada concluída quando:

1. **Funcionalidade**: Todos os testes passam
2. **Qualidade**: Código duplicado foi eliminado
3. **Manutenibilidade**: Código está mais limpo e organizado
4. **Documentação**: Mudanças estão documentadas
5. **Revisão**: Código foi revisado e aprovado

---

## 🚀 Benefícios Esperados

Após a implementação de todas as tarefas:

- **Redução de Duplicação**: De ~15% para ~3%
- **Melhoria na Manutenibilidade**: Mudanças em validações afetam apenas 1 local
- **Aumento da Testabilidade**: Utilitários podem ser testados independentemente
- **Consistência**: Comportamento padronizado em todo o sistema
- **Performance**: Menos código duplicado = menos overhead
- **Developer Experience**: Código mais fácil de entender e modificar

---

## 📝 Notas Importantes

1. **Compatibilidade**: Manter compatibilidade com código existente
2. **Testes**: Sempre executar testes antes e depois das mudanças
3. **Documentação**: Atualizar documentação conforme necessário
4. **Revisão**: Fazer code review de todas as mudanças
5. **Rollback**: Ter plano de rollback para cada mudança

---

**Meta Final**: Elevar a nota do princípio DRY de **6/10** para **9/10** através da eliminação sistemática de duplicações e criação de utilitários reutilizáveis.
