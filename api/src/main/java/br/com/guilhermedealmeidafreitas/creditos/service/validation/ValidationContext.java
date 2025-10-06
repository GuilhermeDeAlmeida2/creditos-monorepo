package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contexto que gerencia e executa estratégias de validação.
 * Implementa o Strategy Pattern para centralizar todas as validações.
 */
@Component
public class ValidationContext {
    
    private final Map<Class<?>, ValidationStrategy<?>> strategies;
    private final StringValidationStrategy stringValidationStrategy;
    private final NumberValidationStrategy numberValidationStrategy;
    private final PageableValidationStrategy pageableValidationStrategy;
    
    @Autowired
    public ValidationContext(List<ValidationStrategy<?>> strategyList,
                           StringValidationStrategy stringValidationStrategy,
                           NumberValidationStrategy numberValidationStrategy,
                           PageableValidationStrategy pageableValidationStrategy) {
        this.stringValidationStrategy = stringValidationStrategy;
        this.numberValidationStrategy = numberValidationStrategy;
        this.pageableValidationStrategy = pageableValidationStrategy;
        
        // Inicializar mapa de estratégias
        this.strategies = new ConcurrentHashMap<>();
        for (ValidationStrategy<?> strategy : strategyList) {
            // Registrar estratégias por tipo suportado
            registerStrategy(strategy);
        }
    }
    
    /**
     * Registra uma estratégia de validação.
     * 
     * @param strategy Estratégia a ser registrada
     */
    private void registerStrategy(ValidationStrategy<?> strategy) {
        // Para simplificar, vamos registrar as estratégias conhecidas
        if (strategy instanceof StringValidationStrategy) {
            strategies.put(String.class, strategy);
        } else if (strategy instanceof NumberValidationStrategy) {
            strategies.put(Number.class, strategy);
        } else if (strategy instanceof PageableValidationStrategy) {
            strategies.put(Pageable.class, strategy);
        }
    }
    
    /**
     * Valida um objeto usando a estratégia apropriada.
     * 
     * @param input Objeto a ser validado
     * @throws ValidationException se a validação falhar
     */
    public void validate(Object input) {
        if (input == null) {
            throw new ValidationException("Objeto não pode ser nulo", "ValidationContext", "input");
        }
        
        Class<?> inputType = input.getClass();
        ValidationStrategy<?> strategy = findStrategy(inputType);
        
        if (strategy != null) {
            @SuppressWarnings("unchecked")
            ValidationStrategy<Object> typedStrategy = (ValidationStrategy<Object>) strategy;
            typedStrategy.validate(input);
        }
        // Se não encontrar estratégia específica, não faz nada (comportamento opcional)
    }
    
    /**
     * Encontra a estratégia apropriada para o tipo fornecido.
     * 
     * @param type Tipo do objeto
     * @return Estratégia apropriada ou null se não encontrada
     */
    private ValidationStrategy<?> findStrategy(Class<?> type) {
        // Busca direta
        ValidationStrategy<?> strategy = strategies.get(type);
        if (strategy != null) {
            return strategy;
        }
        
        // Busca por compatibilidade
        for (ValidationStrategy<?> s : strategies.values()) {
            if (s.supports(type)) {
                return s;
            }
        }
        
        return null;
    }
    
    // ===== MÉTODOS CONVENIÊNCIA PARA TIPOS ESPECÍFICOS =====
    
    /**
     * Valida uma string com nome do campo personalizado.
     * 
     * @param input String a ser validada
     * @param fieldName Nome do campo
     * @throws ValidationException se a validação falhar
     */
    public void validateString(String input, String fieldName) {
        stringValidationStrategy.validate(input, fieldName);
    }
    
    /**
     * Valida uma string opcional.
     * 
     * @param input String a ser validada
     * @return String normalizada ou null
     */
    public String validateOptionalString(String input) {
        return stringValidationStrategy.validateOptional(input);
    }
    
    /**
     * Valida um número positivo.
     * 
     * @param input Número a ser validado
     * @param fieldName Nome do campo
     * @throws ValidationException se a validação falhar
     */
    public void validatePositiveNumber(Number input, String fieldName) {
        numberValidationStrategy.validatePositive(input, fieldName);
    }
    
    /**
     * Valida um número dentro de um range.
     * 
     * @param input Número a ser validado
     * @param min Valor mínimo
     * @param max Valor máximo
     * @param fieldName Nome do campo
     * @throws ValidationException se a validação falhar
     */
    public void validateNumberRange(Number input, Number min, Number max, String fieldName) {
        numberValidationStrategy.validateRange(input, min, max, fieldName);
    }
    
    /**
     * Valida e cria um Pageable.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param sortBy Campo para ordenação
     * @param sortDirection Direção da ordenação
     * @return Pageable validado
     */
    public Pageable validateAndCreatePageable(int page, int size, String sortBy, String sortDirection) {
        return pageableValidationStrategy.validateAndCreatePageable(page, size, sortBy, sortDirection);
    }
    
    /**
     * Valida um Pageable existente.
     * 
     * @param pageable Pageable a ser validado
     * @throws ValidationException se a validação falhar
     */
    public void validatePageable(Pageable pageable) {
        pageableValidationStrategy.validate(pageable);
    }
    
    /**
     * Retorna informações sobre as estratégias registradas.
     * 
     * @return Mapa com informações das estratégias
     */
    public Map<String, String> getRegisteredStrategies() {
        Map<String, String> info = new ConcurrentHashMap<>();
        for (Map.Entry<Class<?>, ValidationStrategy<?>> entry : strategies.entrySet()) {
            info.put(entry.getKey().getSimpleName(), entry.getValue().getStrategyName());
        }
        return info;
    }
}
