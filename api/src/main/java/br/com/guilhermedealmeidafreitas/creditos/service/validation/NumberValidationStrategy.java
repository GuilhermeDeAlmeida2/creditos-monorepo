package br.com.guilhermedealmeidafreitas.creditos.service.validation;

import org.springframework.stereotype.Component;

/**
 * Estratégia de validação para campos numéricos.
 * Valida números inteiros e decimais com diferentes regras.
 */
@Component
public class NumberValidationStrategy implements ValidationStrategy<Number> {
    
    private static final String STRATEGY_NAME = "NumberValidation";
    
    @Override
    public void validate(Number input) {
        if (input == null) {
            throw new ValidationException("Número não pode ser nulo", STRATEGY_NAME, "number");
        }
    }
    
    @Override
    public boolean supports(Class<?> type) {
        return Number.class.isAssignableFrom(type) || 
               type == int.class || type == long.class || 
               type == double.class || type == float.class;
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    /**
     * Valida número com nome do campo personalizado.
     * 
     * @param input Número a ser validado
     * @param fieldName Nome do campo para mensagens de erro
     * @throws ValidationException se a validação falhar
     */
    public void validate(Number input, String fieldName) {
        if (input == null) {
            throw new ValidationException(fieldName + " não pode ser nulo", STRATEGY_NAME, fieldName);
        }
    }
    
    /**
     * Valida se um número é positivo.
     * 
     * @param input Número a ser validado
     * @param fieldName Nome do campo para mensagens de erro
     * @throws ValidationException se o número for negativo
     */
    public void validatePositive(Number input, String fieldName) {
        validate(input, fieldName);
        
        if (input.doubleValue() < 0) {
            throw new ValidationException(fieldName + " deve ser um número positivo", STRATEGY_NAME, fieldName);
        }
    }
    
    /**
     * Valida se um número está dentro de um range específico.
     * 
     * @param input Número a ser validado
     * @param min Valor mínimo (inclusivo)
     * @param max Valor máximo (inclusivo)
     * @param fieldName Nome do campo para mensagens de erro
     * @throws ValidationException se o número estiver fora do range
     */
    public void validateRange(Number input, Number min, Number max, String fieldName) {
        validate(input, fieldName);
        
        double value = input.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();
        
        if (value < minValue || value > maxValue) {
            throw new ValidationException(
                String.format("%s deve estar entre %s e %s", fieldName, min, max), 
                STRATEGY_NAME, 
                fieldName
            );
        }
    }
    
    /**
     * Valida parâmetros de paginação.
     * 
     * @param page Número da página
     * @param size Tamanho da página
     * @param maxSize Tamanho máximo permitido
     * @throws ValidationException se os parâmetros forem inválidos
     */
    public void validatePaginationParams(int page, int size, int maxSize) {
        if (page < 0) {
            throw new ValidationException("Número da página deve ser maior ou igual a 0", STRATEGY_NAME, "page");
        }
        
        if (size <= 0) {
            throw new ValidationException("Tamanho da página deve ser maior que 0", STRATEGY_NAME, "size");
        }
        
        if (size > maxSize) {
            throw new ValidationException(
                String.format("Tamanho da página não pode ser maior que %d", maxSize), 
                STRATEGY_NAME, 
                "size"
            );
        }
    }
}
